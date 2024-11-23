import { tool } from '@langchain/core/tools';
import { z } from 'zod';
import { getModal } from './chatModal';
import { HumanMessage, SystemMessage } from '@langchain/core/messages';

const taskDecompositionSchema = z.object({
  task: z.string().describe('The task to decompose.'),
});

export const taskDecompositionTool = tool(
  async ({ task }) => {
    console.log('Start: Task Decomposition');
    return await getModal().invoke([
      new SystemMessage(
        `Your job is to decompose the task of the user into small bare minimum steps to solve his coding problem. 
        The project setup is already done, we just need to implement a feature or fix a bug.
        Keep the steps like a bullet point list, short and concise.
        Create a maximum of 3 steps.`
      ),
      new HumanMessage(task),
    ]);
  },
  {
    name: 'taskDecomposition',
    description: '2. Decomposes a task into smaller subtasks.',
    schema: taskDecompositionSchema,
  }
);
