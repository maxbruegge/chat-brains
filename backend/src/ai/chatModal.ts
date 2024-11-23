import { ChatOpenAI } from '@langchain/openai';

export const getModal = () =>
  new ChatOpenAI({
    model: 'gpt-4o',
    temperature: 0.5,
  });
