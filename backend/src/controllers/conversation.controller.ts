import { Request, Response, NextFunction } from 'express';
import {
  getConversationById,
  insertConversation,
} from '../repositories/conversation.repository';

class ConversationController {
  async getConversation(
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<any> {
    try {
      const { id } = req.query;

      if (!id) {
        return res
          .status(400)
          .json({ success: false, message: 'ID is required.' });
      }

      const conversation = await getConversationById(id as string);
      if (!conversation) {
        return res
          .status(404)
          .json({ success: false, message: 'Conversation not found.' });
      }

      res.status(200).json({ success: true, data: conversation });
    } catch (error) {
      next(error); // Pass the error to the global error handler
    }
  }

  async createConversation(
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<any> {
    try {
      const { title } = req.body;
      const userId = req.user?.id;

      if (!title) {
        return res
          .status(400)
          .json({ success: false, message: 'Title is required.' });
      }

      const newConversation = await insertConversation({ title, userId });
      res.status(201).json({ success: true, data: newConversation });
    } catch (error) {
      next(error);
    }
  }
}

export const conversationController = new ConversationController();
