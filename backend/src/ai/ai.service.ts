import {
  AIMessage,
  HumanMessage,
  SystemMessage,
  ToolMessage,
} from '@langchain/core/messages';
import { getModal } from './chatModal';
import { taskDecompositionTool } from './taskDecompositionTool';
import { retrieverTool } from './retrieverTool';
import { appendCodeTool } from './appendCodeTool';

export class AIService {
  private messages: (HumanMessage | SystemMessage | ToolMessage | AIMessage)[] =
    [
      new SystemMessage(
        `You are a conversational chatbot that speak with a user to solve his coding problem. 
        Use the tools and answer with short and concise messages which are spoken.
        1. Call the retriever to get relevant files for the query.
        2. Call taskDecomposition to decompose the task into smaller subtasks.
        3. Discuss the implementation for the 1. step with the user.
        4. Call appendCode to save the code snippet for the step.
        5. Repeat the process for the next steps.
        `
      ),
    ];

  async runAI(message: string, isAI: boolean = false) {
    if (isAI) {
      this.messages.push(new AIMessage(message));
    } else {
      this.messages.push(new HumanMessage(message));
    }

    console.log('Messages: ', this.messages);

    const modelWithTools = getModal().bindTools([
      taskDecompositionTool,
      retrieverTool,
      appendCodeTool,
    ]);

    const toolsByName = {
      taskDecomposition: taskDecompositionTool,
      retriever: retrieverTool,
      appendCode: appendCodeTool,
    };

    const result = await modelWithTools.invoke(this.messages);

    console.log('Result: ', result);

    this.messages.push(result);

    for (const toolCall of result?.tool_calls ?? []) {
      const selectedTool =
        toolsByName[toolCall?.name as keyof typeof toolsByName];
      const toolMessage = await selectedTool.invoke(toolCall);
      this.messages.push(toolMessage);
    }

    if (result?.tool_calls?.length ?? 0 > 0) {
      this.runAI(result.content.toString(), true);
    } else {
      return result;
    }
  }
}
