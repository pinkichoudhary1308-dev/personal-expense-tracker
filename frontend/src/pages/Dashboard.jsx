import { useEffect, useState } from "react";

import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts";

import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";

const COLORS = {
    Income: "#16a34a",
    Expense: "#dc2626"
};

function Dashboard() {
    const { user } = useAuth();
    const [summary, setSummary] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadDashboard = async () => {

            try {
                const [summaryResponse, transactionsResponse] =
                    await Promise.all([
                        api.get("/transactions/summary"),
                        api.get(
                            "/transactions?page=0&size=5&sortBy=transactionDate&direction=desc"
                        )
                    ]);

                setSummary(summaryResponse.data);
                setTransactions(
                    transactionsResponse.data.transactions
                );

            } catch (error) {
                console.error(error);
                setError(
                    "Unable to load dashboard data."
                );
            } finally {
                setLoading(false);
            }
        };
        loadDashboard();

    }, []);

    if (loading) {
        return (
            <>
                <Navbar />
                <div className="dashboard-loading">
                    Loading dashboard...
                </div>
            </>
        );
    }

    const chartData = summary
        ? [
            {
                name: "Income",
                value: Number(summary.totalIncome)
            },
            {
                name: "Expense",
                value: Number(summary.totalExpense)
            }
        ]
        : [];

    return (
        <div>
            <Navbar />
            <main className="dashboard-container">
                <div className="dashboard-header">
                    <div>
                        <h1>
                            Welcome, {user?.name}
                        </h1>
                        <p>
                            Here's your financial overview
                        </p>
                    </div>
                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                {summary && (
                    <div className="summary-grid">
                        <div className="summary-card">
                            <p>Total Balance</p>
                            <h2>
                                ₹{Number(summary.balance).toFixed(2)}
                            </h2>
                        </div>

                        <div className="summary-card">
                            <p>Total Income</p>
                            <h2>
                                ₹{Number(summary.totalIncome).toFixed(2)}
                            </h2>
                        </div>

                        <div className="summary-card">
                            <p>Total Expense</p>
                            <h2>
                                ₹{Number(summary.totalExpense).toFixed(2)}
                            </h2>
                        </div>
                    </div>
                )}

                <div className="dashboard-grid">
                    <div className="dashboard-card">
                        <h2>
                            Income vs Expense
                        </h2>
                        <div className="chart-container">

                            <div className="expense-chart">

                                <div className="chart-wrapper">
                                    <ResponsiveContainer
                                        width="100%"
                                        height="100%"
                                    >
                                        <PieChart>
                                            <Pie
                                                data={chartData}
                                                dataKey="value"
                                                nameKey="name"
                                                cx="50%"
                                                cy="50%"
                                                outerRadius="70%"
                                            >
                                                {chartData.map(
                                                    (entry, index) => (
                                                        <Cell
                                                            key={`cell-${index}`}
                                                            fill={COLORS[entry.name]}
                                                        />
                                                    )
                                                )}
                                            </Pie>
                                            <Tooltip />
                                        </PieChart>
                                    </ResponsiveContainer>
                                </div>

                                <div className="chart-legend">
                                    <div className="legend-item">
                                        <span className="legend-color expense-color"></span>
                                        <span>
                                            Expense
                                        </span>
                                    </div>

                                    <div className="legend-item">
                                        <span className="legend-color income-color"></span>
                                        <span>
                                            Income
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="dashboard-card">
                        <div className="card-header">
                            <h2>
                                Recent Transactions
                            </h2>
                        </div>

                        {transactions.length === 0 ? (
                            <p className="empty-message">
                                No transactions found.
                            </p>
                        ) : (
                            <div className="recent-list">
                                {transactions.map(
                                    (transaction) => (
                                        <div
                                            className="recent-item"
                                            key={transaction.id}
                                        >
                                            <div>
                                                <strong>
                                                    {transaction.category}
                                                </strong>
                                                <p>
                                                    {transaction.description ||
                                                        "No description"}
                                                </p>
                                                <small>
                                                    {transaction.transactionDate}
                                                </small>
                                            </div>

                                            <span
                                                className={
                                                    transaction.type === "INCOME"
                                                        ? "income-text"
                                                        : "expense-text"
                                                }
                                            >
                                                {transaction.type === "INCOME"
                                                    ? "+"
                                                    : "-"
                                                }
                                                ₹{Number(
                                                    transaction.amount
                                                ).toFixed(2)}
                                            </span>
                                        </div>
                                    )
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </main>
        </div>
    );
}

export default Dashboard;