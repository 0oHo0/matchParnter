export type UserType = {
    id: number;
    username?: string;
    userAccount: string;
    avatarUrl?: string;
    description?: string;
    gender: number;
    phone: string;
    email: string;
    userStatus: number;
    userRole: number;
    userCode: string;
    tags: string[];
    createTime: Date;
}