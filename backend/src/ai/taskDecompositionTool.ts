import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';

const taskDecompositionSchema = z.object({
  task: z.string().describe('The task to decompose.'),
});

export const taskDecompositionTool = tool(
  async ({ task }) => {
    return await getModal().invoke([
      new SystemMessage(
        'Your job is to decompose the task of the user into multiple steps to solve his coding problem. Think conceptually what needs to be done.'
      ),
      new HumanMessage(task),
    ]);
  },
  {
    name: 'taskDecomposition',
    description: 'Decomposes a task into smaller subtasks.',
    schema: taskDecompositionSchema,
  }
);
