import { Request, Response, NextFunction } from 'express';
import { userRepository } from '../repositories/user.repository';
import { githubAdapter } from '../adapters/github.adapter';

class GitHubController {
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
  async getApiKey(
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<void> {
    try {
      const userId = req.user.id;
      const key = await userRepository.getApiKey(userId);

      res.status(200).json({ success: true, apiKey: key });
    } catch (error) {
      next(error); // Pass errors to the global error handler
    }
  }

  /**
   * Fetches all branches of a repository.
   */
  async getAllBranches(req: Request, res: Response, next: NextFunction) {
    try {
      const userId = req.user.id;

      const user = await userRepository.getUserById(userId);

      if (!user?.owner || !user?.repo || !user.githubApiKey) {
        res.status(400).json({
          success: false,
          message: 'Please specify "repo", ensure owner and API key are set.',
        });
        return;
      }

      const branches = await githubAdapter.fetchAllBranches(
        user.owner,
        user.repo,
        user.githubApiKey
      );

      res.status(200).json({ success: true, branches });
    } catch (error) {
      next(error); // Pass errors to the global error handler
    }
  }

  /**
   * Fetches all repositories for the user or organization.
   */
  async getAllRepos(req: Request, res: Response, next: NextFunction) {
    try {
      const userId = req.user.id;
      const user = await userRepository.getUserById(userId);

      if (!user?.owner || !user.githubApiKey) {
        res.status(400).json({
          success: false,
          message: 'Ensure owner and API key are set.',
        });
        return;
      }

      const repos = await githubAdapter.fetchAllRepos(
        user.owner,
        user.githubApiKey
      );

      const resultRepos = repos.map((repo) => repo.name);

      res.status(200).json({ success: true, repos: resultRepos });
    } catch (error) {
      next(error); // Pass errors to the global error handler
    }
  }

  /**
   * Fetches all issues for a repository.
   */
  async getAllIssues(req: Request, res: Response, next: NextFunction) {
    try {
      const { repo, state } = req.body;
      const userId = req.user.id;

      const user = await userRepository.getUserById(userId);

      if (!user?.owner || !repo || !user.githubApiKey) {
        res.status(400).json({
          success: false,
          message: 'Please specify "repo", ensure owner and API key are set.',
        });
        return;
      }

      const issues = await githubAdapter.fetchAllIssues(
        user.owner,
        repo,
        user.githubApiKey,
        state || 'open'
      );

      const resultIssues = issues.map((issue, index) => ({
        index,
        title: issue.title,
        body: issue.body,
      }));

      res.status(200).json({ success: true, issues: resultIssues });
    } catch (error) {
      next(error);
    }
  }
}

export const githubController = new GitHubController();
