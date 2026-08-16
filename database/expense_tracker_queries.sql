1. Display all transactions:-
	SELECT * 
	FROM transactions;

2. Find expenses above an amount:-
	SELECT *
	FROM transactions
	WHERE type = 'EXPENSE'
  	AND amount > 5000;

3. Calculate total income:-
	SELECT SUM(amount) AS total_income
	FROM transactions
	WHERE type = 'INCOME' AND user_id=4;

4. Calculate total expense:-
	SELECT SUM(amount) AS total_expense
	FROM transactions
	WHERE type = 'EXPENSE'AND user_id=4;

5. Calculate balance:-
	SELECT
    	COALESCE(SUM(
        	CASE
        	    WHEN type = 'INCOME' THEN amount
       		    WHEN type = 'EXPENSE' THEN -amount
     		    ELSE 0
       		END
   	), 0) AS balance
	FROM transactions WHERE user_id=4;

6. Group expenses by category:-
	SELECT
	    category,
	    SUM(amount) AS total_expense
	FROM transactions
	WHERE type = 'EXPENSE' AND user_id=4
	GROUP BY category
	ORDER BY total_expense DESC;

7. Find monthly transactions:-
	SELECT *
	FROM transactions
	WHERE YEAR(transaction_date) = 2026
	AND MONTH(transaction_date) = 8;

8. Join users with transactions:-
	SELECT
	    u.id AS user_id,
	    u.name,
	    u.email,
	    t.id AS transaction_id,
	    t.type,
	    t.category,
	    t.amount,
	    t.description,
	    t.transaction_date
	FROM users u
	JOIN transactions t
	    ON u.id = t.user_id;
