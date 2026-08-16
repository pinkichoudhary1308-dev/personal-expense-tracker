import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(() => {

        const storedUser = localStorage.getItem("user");
        
        return storedUser
            ? JSON.parse(storedUser)
            : null;
    });

    const login = (loginResponse) => {
        localStorage.setItem(
            "token",
            loginResponse.token
        );

        localStorage.setItem(
            "user",
            JSON.stringify({
                userId: loginResponse.userId,
                name: loginResponse.name,
                email: loginResponse.email,
            })
        );

        setUser({
            userId: loginResponse.userId,
            name: loginResponse.name,
            email: loginResponse.email,
        });
    };

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};