-- ==========================================
-- МІГРАЦІЯ: Додавання системи запитів на допомогу
-- ==========================================

-- 1. Додавання поля isOpen до таблиці countries (якщо ще не існує)
-- Це поле вказує чи країна приймає нові запити
ALTER TABLE countries
    ADD COLUMN IF NOT EXISTS is_open BOOLEAN DEFAULT TRUE;

-- Встановлюємо всі існуючі країни як відкриті
UPDATE countries SET is_open = TRUE WHERE is_open IS NULL;

-- 2. Створення таблиці aid_requests
CREATE TABLE IF NOT EXISTS aid_requests (
                                            id BIGSERIAL PRIMARY KEY,
                                            weapon_type VARCHAR(100) NOT NULL,
    weapon_name VARCHAR(200) NOT NULL,
    requested_quantity INTEGER NOT NULL CHECK (requested_quantity > 0),
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('URGENT', 'HIGH', 'MEDIUM', 'LOW')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DELIVERED')),
    request_date DATE NOT NULL DEFAULT CURRENT_DATE,
    request_reason VARCHAR(500),
    rejection_reason VARCHAR(500),
    requesting_country_id BIGINT NOT NULL REFERENCES countries(id) ON DELETE CASCADE,
    donor_country_id BIGINT REFERENCES countries(id) ON DELETE SET NULL,
    period_id BIGINT NOT NULL REFERENCES periods(id) ON DELETE CASCADE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Індекси для покращення продуктивності
CREATE INDEX IF NOT EXISTS idx_aid_requests_status ON aid_requests(status);
CREATE INDEX IF NOT EXISTS idx_aid_requests_priority ON aid_requests(priority);
CREATE INDEX IF NOT EXISTS idx_aid_requests_requesting_country ON aid_requests(requesting_country_id);
CREATE INDEX IF NOT EXISTS idx_aid_requests_donor_country ON aid_requests(donor_country_id);
CREATE INDEX IF NOT EXISTS idx_aid_requests_period ON aid_requests(period_id);
CREATE INDEX IF NOT EXISTS idx_aid_requests_date ON aid_requests(request_date DESC);

-- ==========================================
-- ТЕСТОВІ ДАНІ
-- ==========================================

-- Перевірка та додавання країни України (якщо не існує)
INSERT INTO countries (name, total_aid_usd, is_open)
VALUES ('Ukraine', 0, TRUE)
    ON CONFLICT (name) DO NOTHING;

-- Отримуємо ID країн для прикладів
DO $$
DECLARE
ukraine_id BIGINT;
    usa_id BIGINT;
    germany_id BIGINT;
    period_usa_id BIGINT;
    period_germany_id BIGINT;
BEGIN
    -- Отримуємо ID країн
SELECT id INTO ukraine_id FROM countries WHERE name = 'Ukraine' LIMIT 1;
SELECT id INTO usa_id FROM countries WHERE name = 'USA' LIMIT 1;
SELECT id INTO germany_id FROM countries WHERE name = 'Germany' LIMIT 1;

-- Отримуємо ID періодів
SELECT id INTO period_usa_id FROM periods WHERE country_id = usa_id LIMIT 1;
SELECT id INTO period_germany_id FROM periods WHERE country_id = germany_id LIMIT 1;

-- Приклад 1: URGENT запит від України до USA (PENDING)
IF ukraine_id IS NOT NULL AND usa_id IS NOT NULL AND period_usa_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id
        ) VALUES (
            'Air Defense',
            'Patriot Missile System',
            3,
            'URGENT',
            'PENDING',
            CURRENT_DATE - INTERVAL '2 days',
            'Critical air defense needed for major cities',
            ukraine_id,
            usa_id,
            period_usa_id
        );
END IF;

    -- Приклад 2: HIGH запит від України до Germany (PENDING)
    IF ukraine_id IS NOT NULL AND germany_id IS NOT NULL AND period_germany_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id
        ) VALUES (
            'Vehicles',
            'Leopard 2 Main Battle Tank',
            50,
            'HIGH',
            'PENDING',
            CURRENT_DATE - INTERVAL '5 days',
            'Tank reinforcement for eastern front operations',
            ukraine_id,
            germany_id,
            period_germany_id
        );
END IF;

    -- Приклад 3: MEDIUM запит без призначеного донора
    IF ukraine_id IS NOT NULL AND period_usa_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id
        ) VALUES (
            'Ammunition',
            '155mm Artillery Shells',
            500000,
            'MEDIUM',
            'PENDING',
            CURRENT_DATE - INTERVAL '7 days',
            'Ammunition resupply for ongoing operations',
            ukraine_id,
            NULL, -- Донора ще не призначено
            period_usa_id
        );
END IF;

    -- Приклад 4: APPROVED запит (вже схвалений)
    IF ukraine_id IS NOT NULL AND usa_id IS NOT NULL AND period_usa_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id
        ) VALUES (
            'Artillery',
            'M777 Howitzer',
            20,
            'HIGH',
            'APPROVED',
            CURRENT_DATE - INTERVAL '15 days',
            'Artillery support for counter-offensive operations',
            ukraine_id,
            usa_id,
            period_usa_id
        );
END IF;

    -- Приклад 5: REJECTED запит
    IF ukraine_id IS NOT NULL AND germany_id IS NOT NULL AND period_germany_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id,
            rejection_reason
        ) VALUES (
            'Aircraft',
            'F-16 Fighting Falcon',
            100,
            'URGENT',
            'REJECTED',
            CURRENT_DATE - INTERVAL '20 days',
            'Air superiority requirements',
            ukraine_id,
            usa_id,
            period_usa_id,
            'Insufficient training infrastructure and timeline constraints'
        );
END IF;

    -- Приклад 6: LOW пріоритет запит
    IF ukraine_id IS NOT NULL AND usa_id IS NOT NULL AND period_usa_id IS NOT NULL THEN
        INSERT INTO aid_requests (
            weapon_type, weapon_name, requested_quantity, priority, status,
            request_date, request_reason, requesting_country_id, donor_country_id, period_id
        ) VALUES (
            'Vehicles',
            'Humvee Multipurpose Vehicle',
            200,
            'LOW',
            'PENDING',
            CURRENT_DATE - INTERVAL '3 days',
            'Logistics and transportation support',
            ukraine_id,
            usa_id,
            period_usa_id
        );
END IF;

END $$;

-- ==========================================
-- ДЕМОНСТРАЦІЯ БІЗНЕС-ЛОГІКИ
-- ==========================================

-- Приклад: Закриваємо країну (ця країна більше не приймає запити)
-- UPDATE countries SET is_open = FALSE WHERE name = 'Poland';

-- Приклад: Відкриваємо країну
-- UPDATE countries SET is_open = TRUE WHERE name = 'Poland';

-- ==========================================
-- КОРИСНІ ЗАПИТИ ДЛЯ ПЕРЕВІРКИ
-- ==========================================

-- Перегляд всіх запитів з деталями
SELECT
    ar.id,
    ar.weapon_name,
    ar.requested_quantity,
    ar.priority,
    ar.status,
    ar.request_date,
    rc.name AS requesting_country,
    COALESCE(dc.name, 'Not assigned') AS donor_country,
    p.period_name AS period
FROM aid_requests ar
         JOIN countries rc ON ar.requesting_country_id = rc.id
         LEFT JOIN countries dc ON ar.donor_country_id = dc.id
         JOIN periods p ON ar.period_id = p.id
ORDER BY ar.request_date DESC;

-- Статистика по статусам
SELECT
    status,
    COUNT(*) AS count,
    SUM(requested_quantity) AS total_quantity
FROM aid_requests
GROUP BY status
ORDER BY count DESC;

-- Статистика по пріоритетам
SELECT
    priority,
    COUNT(*) AS count,
    SUM(requested_quantity) AS total_quantity
FROM aid_requests
GROUP BY priority
ORDER BY
    CASE priority
    WHEN 'URGENT' THEN 1
    WHEN 'HIGH' THEN 2
    WHEN 'MEDIUM' THEN 3
    WHEN 'LOW' THEN 4
END;

-- Запити що очікують розгляду
SELECT
    ar.weapon_name,
    ar.requested_quantity,
    ar.priority,
    rc.name AS from_country,
    COALESCE(dc.name, 'Not assigned') AS to_country,
    ar.request_date
FROM aid_requests ar
         JOIN countries rc ON ar.requesting_country_id = rc.id
         LEFT JOIN countries dc ON ar.donor_country_id = dc.id
WHERE ar.status = 'PENDING'
ORDER BY
    CASE ar.priority
        WHEN 'URGENT' THEN 1
        WHEN 'HIGH' THEN 2
        WHEN 'MEDIUM' THEN 3
        WHEN 'LOW' THEN 4
        END,
    ar.request_date;

-- Перевірка статусу країн (відкрита/зачинена)
SELECT
    name,
    CASE
        WHEN is_open THEN '✅ Відкрита'
        ELSE '❌ Зачинена'
        END AS status
FROM countries
ORDER BY name;

COMMIT;