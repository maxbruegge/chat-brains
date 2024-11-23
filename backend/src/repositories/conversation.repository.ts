import { Types } from 'mongoose';
import {
  IConversation,
  Conversation,
  IConversationFiles,
} from '../models/conversation';

class ConversationRepository {
  /**
   * Retrieves a conversation by ID.
   * @param id - The ID of the conversation to fetch.
   * @returns The conversation document or null if not found.
   */
  async getConversationById(
    id: string,
    userId: Types.ObjectId
  ): Promise<IConversation | null> {
    return Conversation.findOne({ _id: id, userId });
  }

  /**
   * Retrieves all conversations for a specific user.
   * @param userId - The ID of the user whose conversations to fetch.
   * @returns An array of conversation documents.
   */
  async getConversationsByUserId(
    userId: Types.ObjectId
  ): Promise<IConversation[]> {
    return Conversation.find({ userId }, { files: 0, messages: 0 });
  }

  /**
   * Inserts a new conversation into the database.
   * @param data - The conversation data to insert.
   * @returns The newly created conversation document.
   */
  async insertConversation(data: {
    title: string;
    userId: Types.ObjectId;
  }): Promise<IConversation> {
    const conversation = new Conversation(data);
    return conversation.save();
  }

  /**
   * Adds messages to an existing conversation.
   * If a file with the same filename already exists, it will be overwritten.
   * Otherwise, the file will be added to the conversation.
   * @param id - The ID of the conversation to update.
   * @param files - An array of files to add to the conversation.
   * @returns A promise that resolves to the updated conversation document.
   */
  async addMessageToConversation(
    id: Types.ObjectId,
    files: IConversationFiles[]
  ) {
    // Iterate over each file
    for (const file of files) {
      await Conversation.updateOne(
        { _id: id, 'files.filename': file.filename }, // Find a matching filename
        {
          $set: { 'files.$': file }, // Overwrite the matching file
        }
      );

      // Add the file if it doesn't already exist
      await Conversation.updateOne(
        { _id: id, 'files.filename': { $ne: file.filename } }, // No matching filename
        {
          $push: { files: file }, // Add the new file
        }
      );
    }

    // Return the updated conversation
    return Conversation.findById(id);
  }
}

export const conversationRepository = new ConversationRepository();
