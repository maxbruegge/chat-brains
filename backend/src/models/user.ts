import mongoose, { Schema, Document } from 'mongoose';

export interface GetUser {
  id?: string;
  email?: string;
}

// Define the interface for a user document
export interface IUser extends Document {
  username: string;
  email: string;
  password: string;
  githubApiKey?: string;
  owner?: string;
  repo?: string;
}

// Create a schema for the user collection
const UserSchema: Schema = new Schema({
  username: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  githubApiKey: { type: String },
  owner: { type: String },
  repo: { type: String },
});

// Export the model for the user collection
export default mongoose.model<IUser>('User', UserSchema, 'user');
