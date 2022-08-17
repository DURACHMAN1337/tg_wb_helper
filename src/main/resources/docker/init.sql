SELECT 'CREATE DATABASE telegram_helper'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'telegram_helper')\gexec