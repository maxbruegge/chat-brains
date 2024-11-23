import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { conversationRepository } from '../repositories/conversation.repository';

const appendCodeSchema = z.object({
  changedFiles: z.array(
    z.object({
      filename: z.string().describe('The name of the file.'),
      content: z.string().describe('The complete, edited content of the file.'),
    })
  ),
});

export const appendCodeTool = tool(
  async ({ changedFiles }) => {
    console.log('Start: Append Code');

    // TODO: @Maxi
    try {
      //   conversationRepository.addMessageToConversation(_, changedFiles);
      //   return 'The code has been saved.';
    } catch {
      throw new Error('Changes could not be saved');
    }
  },
  {
    name: 'appendCode',
    description:
      'When you want to finalize a code snippet for a step call this tool.',
    schema: appendCodeSchema,
  }
);
