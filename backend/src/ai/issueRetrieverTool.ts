import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';
import { Types } from 'mongoose';
import { userRepository } from '../repositories/user.repository';
import { githubAdapter } from '../adapters/github.adapter';

const issueRetrieverSchema = z.object({
  task: z.string().describe('The task to solve.'),
});

const responseSchema = z.object({
  issueIndex: z
    .number()
    .nullable()
    .describe('The index of the selected issue.'),
});

export const issueRetrieverTool = (userId: Types.ObjectId) =>
  tool(
    async ({ task }): Promise<string | undefined> => {
      console.log('Start: issueRetriever');
      const user = await userRepository.getUserById(userId);
      if (!user?.owner || !user?.repo || !user?.githubApiKey) {
        throw new Error('User has not set up the GitHub API key.');
      }

      const allIssues = await githubAdapter.fetchAllIssues(
        user.owner,
        user.repo,
        user.githubApiKey
      );

      if (allIssues.length === 0) {
        return undefined;
      }

      const issues = await getModal()
        .withStructuredOutput(responseSchema)
        .invoke([
          new SystemMessage(
            `Your job is to select the correct issue, which is fitting to the task from the list of all open issues.
          
          Here are all the available issues:

          ${allIssues.map((issue) => `- ${JSON.stringify(issue)}`).join(',\n')}
          `
          ),
          new HumanMessage(task),
        ]);

      return JSON.stringify(
        allIssues.find((_, index) => index === issues.issueIndex)
      );
    },
    {
      name: 'issueRetriever',
      description:
        '1. Retrieves the relevant issue from github to the users request.',
      schema: issueRetrieverSchema,
    }
  );
