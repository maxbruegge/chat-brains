import axios from 'axios';

const GITHUB_API_URL = 'https://api.github.com';

interface GitHubContent {
  name: string;
  path: string;
  type: 'file' | 'dir';
  download_url: string | null;
  content?: string; // Base64 content (present in file response)
}

interface GitHubRepoMetadata {
  id: number;
  name: string;
  full_name: string;
  private: boolean;
  owner: {
    login: string;
    id: number;
  };
  html_url: string;
  description: string | null;
  fork: boolean;
  created_at: string;
  updated_at: string;
  pushed_at: string;
  default_branch: string;
  language: string | null;
  visibility: string;
  open_issues_count: number;
  forks_count: number;
  watchers_count: number;
}

class GitHubAdapter {
  /**
   * Fetches metadata about a GitHub repository.
   * @param owner - The repository owner (username or organization).
   * @param repo - The repository name.
   * @param token - The GitHub personal access token.
   * @returns Metadata about the repository.
   */
  async fetchRepositoryMetadata(
    owner: string,
    repo: string,
    token: string
  ): Promise<GitHubRepoMetadata> {
    const url = `${GITHUB_API_URL}/repos/${owner}/${repo}`;
    const response = await axios.get<GitHubRepoMetadata>(url, {
      headers: {
        Authorization: `Bearer ${token}`,
        'X-GitHub-Api-Version': '2022-11-28',
      },
    });
    return response.data;
  }

  /**
   * Fetches the contents of a GitHub repository directory recursively.
   * @param owner - The repository owner (username or organization).
   * @param repo - The repository name.
   * @param path - The path within the repository (empty string for root).
   * @param token - The GitHub personal access token.
   * @returns An array of file paths with their absolute paths.
   */
  async fetchAllFilesFromRepo(
    owner: string,
    repo: string,
    path: string = '',
    token: string
  ): Promise<string[]> {
    const url = `${GITHUB_API_URL}/repos/${owner}/${repo}/contents/${path}`;

    try {
      const response = await axios.get<GitHubContent[]>(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          'X-GitHub-Api-Version': '2022-11-28',
        },
      });

      const contents = response.data;
      const filePaths: string[] = [];

      for (const content of contents) {
        if (content.type === 'file') {
          // Add the absolute path of the file
          filePaths.push(`/${content.path}`);
        } else if (content.type === 'dir') {
          // Recursively fetch contents of subdirectories
          const subDirFiles = await this.fetchAllFilesFromRepo(
            owner,
            repo,
            content.path,
            token
          );
          filePaths.push(...subDirFiles);
        }
      }

      return filePaths;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `GitHub API Error (${error.response?.status}): ${
            error.response?.data?.message || error.message
          }`
        );
      }
      throw error;
    }
  }

  /**
   * Fetches the content of a single file from a GitHub repository.
   * @param owner - The repository owner.
   * @param repo - The repository name.
   * @param filePath - The file's path in the repository.
   * @param token - The GitHub personal access token.
   * @returns The decoded content of the file.
   */
  async fetchFileContent(
    owner: string,
    repo: string,
    filePath: string,
    token: string
  ): Promise<string> {
    const url = `${GITHUB_API_URL}/repos/${owner}/${repo}/contents/${filePath}`;

    try {
      const response = await axios.get<GitHubContent>(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          'X-GitHub-Api-Version': '2022-11-28',
        },
      });

      const content = response.data.content;
      if (!content) {
        throw new Error(
          `File at path ${filePath} does not contain any content.`
        );
      }

      // Decode Base64 content
      return Buffer.from(content, 'base64').toString('utf-8');
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `GitHub API Error (${error.response?.status}): ${
            error.response?.data?.message || error.message
          }`
        );
      }
      throw error;
    }
  }

  /**
   * Fetches the content of multiple files from a GitHub repository.
   * @param owner - The repository owner.
   * @param repo - The repository name.
   * @param filePaths - A list of file paths to fetch.
   * @param token - The GitHub personal access token.
   * @returns A map of file paths to their decoded content.
   */
  async fetchMultipleFileContents(
    owner: string,
    repo: string,
    token: string,
    filePaths: string[]
  ): Promise<Record<string, string>> {
    const fileContents: Record<string, string> = {};

    for (const filePath of filePaths) {
      try {
        const content = await this.fetchFileContent(
          owner,
          repo,
          filePath,
          token
        );
        fileContents[filePath] = content;
      } catch (error) {
        console.error(`Failed to fetch content for ${filePath}:`, error);
        fileContents[filePath] = `Error: ${error}`;
      }
    }

    return fileContents;
  }

  /**
   * Fetches all branches of a GitHub repository.
   * @param owner - The repository owner (username or organization).
   * @param repo - The repository name.
   * @param token - The GitHub personal access token.
   * @returns A list of branch names.
   */
  async fetchAllBranches(
    owner: string,
    repo: string,
    token: string
  ): Promise<string[]> {
    const url = `${GITHUB_API_URL}/repos/${owner}/${repo}/branches`;

    try {
      const response = await axios.get<{ name: string }[]>(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          'X-GitHub-Api-Version': '2022-11-28',
        },
      });

      // Extract branch names from the response
      return response.data.map((branch) => branch.name);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `GitHub API Error (${error.response?.status}): ${
            error.response?.data?.message || error.message
          }`
        );
      }
      throw error;
    }
  }

  /**
   * Fetches all repositories for a user or organization.
   * @param owner - The user or organization name.
   * @param token - The GitHub personal access token.
   * @returns A list of repositories.
   */
  async fetchAllRepos(
    owner: string,
    token: string
  ): Promise<GitHubRepoMetadata[]> {
    const url = `${GITHUB_API_URL}/users/${owner}/repos`;

    try {
      const response = await axios.get<GitHubRepoMetadata[]>(url, {
        headers: {
          Authorization: `Bearer ${token}`,
          'X-GitHub-Api-Version': '2022-11-28',
        },
      });

      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        throw new Error(
          `GitHub API Error (${error.response?.status}): ${
            error.response?.data?.message || error.message
          }`
        );
      }
      throw error;
    }
  }
}

export const githubAdapter = new GitHubAdapter();
