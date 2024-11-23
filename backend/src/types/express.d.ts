import { ObjectId } from "mongoose";

export {};

declare global {
  namespace Express {
    interface Request {
      user: { email: string; id: ObjectId };
    }
  }
}
