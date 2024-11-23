import { model, ObjectId, Schema } from 'mongoose';

export interface IConversation extends Document {
  userId: ObjectId;
  title: string;
  createdAt: Date;
}

export const ConversationSchema = new Schema<IConversation>(
  {
    userId: { type: Schema.Types.ObjectId, required: true },
    title: { type: String, required: true },
  },
  { timestamps: true } // Automatically manage createdAt and updatedAt fields
);

export const Conversation = model<IConversation>(
  'Conversation',
  ConversationSchema
);
