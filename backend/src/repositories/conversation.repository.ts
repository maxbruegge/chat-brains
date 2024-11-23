import { Document, ObjectId, Schema, model } from 'mongoose';
import { IConversation, Conversation } from '../models/conversation';

/**
 * Retrieves a conversation by ID.
 * @param id - The ID of the conversation to fetch.
 */
export const getConversationById = async (
  id: string
): Promise<IConversation | null> => {
  return Conversation.findById(id);
};

/**
 * Inserts a new conversation into the database.
 * @param data - The conversation data to insert.
 */
export const insertConversation = async (data: {
  title: string;
  userId: ObjectId;
}): Promise<IConversation> => {
  const conversation = new Conversation(data);
  return conversation.save();
};
