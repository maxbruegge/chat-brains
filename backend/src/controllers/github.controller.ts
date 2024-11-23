import { Request, Response, NextFunction } from 'express';
import { userRepository } from '../repositories/user.repository';
import { githubAdapter } from '../adapters/github.adapter';

class GitHubController {
  private apiKey: string | null = null; // Store the GitHub API key in memory

  /**
   * Sets the GitHub API key.
   */
  async setApiKey(req: Request, res: Response, next: NextFunction) {
    try {
      const { githubApiKey, owner, repo } = req.body;

      if (!githubApiKey || !owner || !repo) {
        res.status(400).json({
          success: false,
          message: 'Please specify "githubApiKey", "owner", and "repo".',
        });
        return;
      }

      // Test connection
      try {
        await githubAdapter.fetchRepositoryMetadata(owner, repo, githubApiKey);
      } catch (error) {
        res
          .status(401)
          .json({ success: false, message: 'Invalid credentials' });
        return;
      }

      await userRepository.setGithubApiKey(
        req.user.id,
        githubApiKey,
        owner,
        repo
      );
      res
        .status(200)
        .json({ success: true, message: 'API key set successfully.' });
    } catch (error) {
      next(error); // Pass errors to the global error handler
    }
  }

  /**
   * Retrieves the stored GitHub API key (for debugging or validation).
   */
  getApiKey(req: Request, res: Response, next: NextFunction): void {
    try {
      if (!this.apiKey) {
        res.status(404).json({ success: false, message: 'API key not set.' });
        return;
      }

      res.status(200).json({ success: true, apiKey: this.apiKey });
    } catch (error) {
      next(error); // Pass errors to the global error handler
    }
  }

  /**
   * Middleware to attach the API key to outgoing requests.
   */
  getApiKeyForRequest(): string | null {
    return this.apiKey;
  }
}

export const githubController = new GitHubController();
