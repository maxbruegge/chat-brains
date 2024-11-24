import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';
import { Types } from 'mongoose';
import { userRepository } from '../repositories/user.repository';
import { githubAdapter } from '../adapters/github.adapter';

const retrieverSchema = z.object({
  task: z.string().describe('The task to solve.'),
});

const responseSchema = z.object({
  files: z
    .array(z.string())
    .describe('The files that are needed to solve the task.'),
});

export const retrieverTool = (userId: Types.ObjectId) =>
  tool(
    async ({ task }): Promise<string> => {
      console.log('Start: Code Retriever');
      const user = await userRepository.getUserById(userId);
      if (!user?.owner || !user?.repo || !user?.githubApiKey) {
        throw new Error('User has not set up the GitHub API key.');
      }
      const allFileNames = await githubAdapter.fetchAllFilesFromRepo(
        user.owner,
        user.repo,
        '',
        user.githubApiKey
      );

      const files = await getModal()
        .withStructuredOutput(responseSchema)
        .invoke([
          new SystemMessage(
            `Your job is to select the files that you think might be needed to solve the issue of the user.
          
          Here are all the available files:

          ${allFileNames.map((file) => `- ${file}`).join('\n')}
          `
          ),
          new HumanMessage(task),
        ]);

      const fullFiles = await githubAdapter.fetchMultipleFileContents(
        user.owner,
        user.repo,
        user.githubApiKey,
        files.files
      );

      return JSON.stringify(fullFiles) ?? fullFiles.toString() ?? '';
    },
    {
      name: 'retriever',
      description:
        '2. Retrieves relevant code files from a given issue and user request.',
      schema: retrieverSchema,
    }
  );
