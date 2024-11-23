import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';
import { allFiles, code } from './mock';

const retrieverSchema = z.object({
  task: z.string().describe('The task to solve.'),
});

const responseSchema = z.object({
  files: z
    .array(z.string())
    .describe('The files that are needed to solve the task.'),
});

export const retrieverTool = tool(
  async ({ task }): Promise<string> => {
    console.log('Start: Retriever');
    const files = await getModal()
      .withStructuredOutput(responseSchema)
      .invoke([
        new SystemMessage(
          `Your job is to select the files that you think might be needed to solve the issue of the user.
          
          Here are all the available files:

          ${allFiles.map((file) => `- ${file}`).join('\n')}
          `
        ),
        new HumanMessage(task),
      ]);

    const codeFiles = code
      .filter((file) => files.files.includes(file.name))
      .map((file) => JSON.stringify(file))
      .join(',\n');

    return codeFiles.toString();
  },
  {
    name: 'retriever',
    description:
      '1. Retrieves relevant code files from a given issue and user request.',
    schema: retrieverSchema,
  }
);
