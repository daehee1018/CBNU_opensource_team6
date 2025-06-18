import sqlite3
import pymysql
import os
import argparse

parser = argparse.ArgumentParser(description="Import nutrition data into MySQL")
parser.add_argument("--db-host", default=os.environ.get("MYSQL_HOST", "localhost"))
parser.add_argument("--db-port", type=int, default=int(os.environ.get("MYSQL_PORT", "3306")))
parser.add_argument("--db-user", default=os.environ.get("MYSQL_USER", "root"))
parser.add_argument("--db-password", default=os.environ.get("MYSQL_PASSWORD", ""))
parser.add_argument("--db-name", default=os.environ.get("MYSQL_DB", "recommend_diet"))
parser.add_argument("--sqlite", default=os.environ.get("NUTRITION_DB", "Frontend/app/src/main/assets/nutrition_data.db"))
args = parser.parse_args()

# Connect to SQLite
sq_conn = sqlite3.connect(args.sqlite)
sq_cur = sq_conn.cursor()

# Connect to MySQL
my_conn = pymysql.connect(
    host=args.db_host,
    port=args.db_port,
    user=args.db_user,
    password=args.db_password,
    db=args.db_name,
    charset='utf8'
)
my_cur = my_conn.cursor()

# Create food table if not exists
create_sql = """
CREATE TABLE IF NOT EXISTS food (
    food_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(255),
    rep_name VARCHAR(255),
    subcategory VARCHAR(255),
    standard_amount VARCHAR(255),
    image_url VARCHAR(255),
    energy INT,
    protein DOUBLE,
    fat DOUBLE,
    carbohydrate DOUBLE,
    sugar DOUBLE,
    sodium DOUBLE,
    cholesterol DOUBLE,
    saturated_fat DOUBLE
);
"""
my_cur.execute(create_sql)

sq_cur.execute('SELECT * FROM nutrition')
rows = sq_cur.fetchall()

insert_sql = """
INSERT INTO food (name, category, rep_name, subcategory, standard_amount, energy, protein, fat, carbohydrate, sugar, sodium, cholesterol, saturated_fat)
VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
"""
for row in rows:
    values = row[:13]  # ignore weight column if exists
    my_cur.execute(insert_sql, values)

my_conn.commit()
print(f"Inserted {len(rows)} rows into food table")
