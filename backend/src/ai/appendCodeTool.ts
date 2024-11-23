import { tool } from '@langchain/core/tools';
import { z } from 'zod';

const appendCodeSchema = z.object({
  changedFiles: z.array(
    z.object({
      name: z.string().describe('The name of the file.'),
      newContent: z.string().describe('The content of the file.'),
    })
  ),
});

export const appendCodeTool = tool(
  async ({ changedFiles }) => {
    // TODO add to the files conversation.

    return 'The code has been saved.';
  },
  {
    name: 'appendCode',
    description:
      'When you want to finalize a code snippet for a step call this tool.',
    schema: appendCodeSchema,
  }
);
