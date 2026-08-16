import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                Expense Tracker
            </div>
            <div className="navbar-links">
                <Link to="/dashboard">
                    Dashboard
                </Link>
                <Link to="/transactions">
                    Transactions
                </Link>
                <span className="navbar-user">
                    👤{user?.name}
                </span>
                <button
                    className="logout-button"
                    onClick={handleLogout}
                >
                    Logout
                </button>
            </div>
        </nav>
    );
}

export default Navbar;