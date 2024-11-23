import { model, ObjectId, Schema } from 'mongoose';
import { Document } from 'mongoose';

export interface IConversation extends Document {
  userId: ObjectId;
  title: string;
  createdAt: Date;
  files: IConversation[];
  messages: string[];
}

export interface IConversationFiles extends Document {
  filename: string;
  content: string;
}

const ConversationContentSchema = new Schema<IConversationFiles>(
  {
    filename: { type: String, required: true },
    content: { type: String, required: true },
  },
  { _id: false }
);

export const ConversationSchema = new Schema<IConversation>(
  {
    userId: { type: Schema.Types.ObjectId, required: true },
    title: { type: String, required: true },
    files: { type: [ConversationContentSchema], default: [] },
    messages: { type: [String], default: [] },
    createdAt: { type: Date, default: Date.now },
  },
  { timestamps: true }
);

export const Conversation = model<IConversation>(
  'Conversation',
  ConversationSchema
);
