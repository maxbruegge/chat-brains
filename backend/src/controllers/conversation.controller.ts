import { Request, Response, NextFunction } from 'express';
import { conversationRepository } from '../repositories/conversation.repository';
import { Types } from 'mongoose';

class ConversationController {
  async getConversation(
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<any> {
    try {
      const { id } = req.query;
      const userId = req.user?.id;

      if (!id) {
        if (!userId) {
          return res
            .status(400)
            .json({ success: false, message: 'User ID is required.' });
        }
        const conversation =
          await conversationRepository.getConversationsByUserId(userId);
        if (!conversation) {
          return res
            .status(404)
            .json({ success: false, message: 'Conversation not found.' });
        }
        return res.status(200).json({ success: true, data: conversation });
      }

      const conversation = await conversationRepository.getConversationById(
        id as string,
        userId
      );
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

      const newConversation = await conversationRepository.insertConversation({
        title,
        userId,
      });
      res.status(201).json({ success: true, data: newConversation });
    } catch (error) {
      next(error);
    }
  }
  async addToConversation(
    req: Request,
    res: Response,
    next: NextFunction
  ): Promise<any> {
    try {
      const { id } = req.params;
      const conversationId = Types.ObjectId.createFromHexString(id);
      const { files } = req.body;

      if (!files) {
        return res.status(400).json({
          success: false,
          message: '"fileName", "content" is required.',
        });
      }
      
      const updatedConversation =
        await conversationRepository.addMessageToConversation(
          conversationId,
          files
        );

      if (!updatedConversation) {
        return res
          .status(404)
          .json({ success: false, message: 'Conversation not found.' });
      }

      res.status(200).json({ success: true, data: updatedConversation });
    } catch (error) {
      next(error);
    }
  }
}

export const conversationController = new ConversationController();
