import myaxios from "../axios/myaxios";
import { setCurrentUserState } from "../states/user";

export const getCurrentUser =async () => {
    const res = await myaxios.get('/user/current');
    if (res.code === 0) {
        setCurrentUserState(res.data);
        return res.data;
    }
}


