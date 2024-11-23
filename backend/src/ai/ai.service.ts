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
import { Types } from 'mongoose';
import { issueRetrieverTool } from './issueRetrieverTool';

class AiService {
  private messages: (HumanMessage | SystemMessage | ToolMessage | AIMessage)[] =
    [
      new SystemMessage(
        `You are a conversational chatbot that speaks with the user to solve his coding problem.
        The user does not see the code, and you should not say the full code, (only insert it into the tools) 
        but rather explain what you did sololy with the file and function names.
        Use the tools to get and write code. Speak to the user with just a few words, to get more information.

        Follow these steps:
        1. Your first step is to call retrievIssue, and check if you find the one from the user.
        2. Afterwards call the retriever and get relevant files for the query and issue.
        3. Call taskDecomposition to decompose the task into smaller subtasks.
        4. Discuss the implementation for the 1. step with the user.
        5. Call appendCode to save the code snippet for the step.
        6. Repeat the process for the next steps.
        `
      ),
    ];

  async runAI({
    userId,
    message,
    isAI = false,
  }: {
    userId: Types.ObjectId;
    message: string;
    isAI?: boolean;
  }): Promise<any> {
    if (isAI) {
      this.messages.push(new AIMessage(message));
    } else {
      this.messages.push(new HumanMessage(message));
    }

    const appendCode = appendCodeTool(userId);
    const retriever = retrieverTool(userId);
    const issueRetriever = issueRetrieverTool(userId);

    const modelWithTools = getModal().bindTools([
      retriever,
      taskDecompositionTool,
      appendCode,
      issueRetriever,
    ]);

    const toolsByName = {
      retriever: retrieverTool,
      taskDecomposition: taskDecompositionTool,
      appendCode: appendCode,
      issueRetriever: issueRetrieverTool,
    };

    const result = await modelWithTools.invoke(this.messages);


    this.messages.push(result);

    for (const toolCall of result?.tool_calls ?? []) {
      const selectedTool =
        toolsByName[toolCall?.name as keyof typeof toolsByName];
      const toolInstance =
        typeof selectedTool === 'function'
          ? selectedTool(userId)
          : selectedTool;
      const toolMessage = await toolInstance.invoke(toolCall);
      this.messages.push(toolMessage);
    }


    if (result?.tool_calls?.length ?? 0 > 0) {
      return this.runAI({
        message: result.content.toString(),
        userId,
        isAI: true,
      });
    } else {
      return result;
    }
  }
}

export const aiService = new AiService();
