-- Тестові дані для демонстрації
-- Можна виконати через PgAdmin або CRUD форми

-- Countries (вже додані автоматично при старті)
-- USA, Germany, United Kingdom

-- Додаткові країни
INSERT INTO countries (name, total_aid_usd) VALUES ('Poland', 3500000000);
INSERT INTO countries (name, total_aid_usd) VALUES ('France', 2800000000);
INSERT INTO countries (name, total_aid_usd) VALUES ('Canada', 4200000000);

-- Periods для USA (id=1)
INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd)
VALUES (1, '2022 Q1-Q2', '2022-01-01', '2022-06-30', 15000000000);

INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd)
VALUES (1, '2022 Q3-Q4', '2022-07-01', '2022-12-31', 25000000000);

INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd)
VALUES (1, '2023 Full Year', '2023-01-01', '2023-12-31', 35000000000);

-- Periods для Germany (id=2)
INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd)
VALUES (2, '2022-2023', '2022-02-01', '2023-12-31', 28000000000);

-- Periods для UK (id=3)
INSERT INTO periods (country_id, period_name, start_date, end_date, aid_amount_usd)
VALUES (3, '2022-2024', '2022-03-01', NULL, 15000000000);

-- Weapons для USA Q1-Q2 2022 (period_id=1)
INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (1, 'Artillery', 'M777 Howitzer', 90, 2500000, 225000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (1, 'Air Defense', 'Javelin Anti-Tank Missile', 5000, 178000, 890000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (1, 'Ammunition', '155mm Artillery Shells', 800000, 1000, 800000000);

-- Weapons для USA Q3-Q4 2022 (period_id=2)
INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (2, 'Air Defense', 'HIMARS Rocket System', 20, 5600000, 112000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (2, 'Air Defense', 'Patriot Missile System', 1, 1100000000, 1100000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (2, 'Vehicles', 'M1 Abrams Tank', 31, 10000000, 310000000);

-- Weapons для USA 2023 (period_id=3)
INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (3, 'Aircraft', 'F-16 Fighter Jet', 42, 63000000, 2646000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (3, 'Artillery', 'ATACMS Missile', 500, 1500000, 750000000);

-- Weapons для Germany (period_id=4)
INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (4, 'Air Defense', 'Leopard 2 Tank', 18, 12000000, 216000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (4, 'Air Defense', 'IRIS-T Air Defense System', 4, 150000000, 600000000);

-- Weapons для UK (period_id=5)
INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (5, 'Artillery', 'Storm Shadow Cruise Missile', 300, 2000000, 600000000);

INSERT INTO weapons (period_id, weapon_type, weapon_name, quantity, unit_cost_usd, total_cost_usd)
VALUES (5, 'Vehicles', 'Challenger 2 Tank', 14, 5000000, 70000000);

-- Перевірка даних
SELECT 'Countries:' as Info, COUNT(*) as Count FROM countries
UNION ALL
SELECT 'Periods:', COUNT(*) FROM periods
UNION ALL
SELECT 'Weapons:', COUNT(*) FROM weapons;