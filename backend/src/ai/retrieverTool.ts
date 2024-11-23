import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';

const retrieverSchema = z.object({
  steps: z.string().describe('The steps to solve the task.'),
});

const responseSchema = z.object({
  files: z
    .array(z.string())
    .describe('The files that are needed to solve the task.'),
});

export const retrieverTool = tool(
  async ({ steps }) => {
    const files = await getModal()
      .withStructuredOutput(responseSchema)
      .invoke([
        new SystemMessage(
          `Your job is to select the files that you think might be needed to solve the steps.
          
          Here are all the available files:
          ${allFiles.map((file) => `- ${file}`).join('\n')}
          `
        ),
        new HumanMessage(steps),
      ]);

    return JSON.stringify(
      code.filter((file) => files.files.includes(file.name)).join('\n')
    );
  },
  {
    name: 'retriever',
    description: 'Retrieves code files from a list of steps.',
    schema: retrieverSchema,
  }
);
