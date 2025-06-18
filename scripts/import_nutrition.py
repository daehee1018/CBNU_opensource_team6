import sqlite3
import pymysql
import os
import argparse

parser = argparse.ArgumentParser(description="Import nutrition data into MySQL")
parser.add_argument("--db-host", default=os.environ.get("MYSQL_HOST", "localhost"))
parser.add_argument("--db-port", type=int, default=int(os.environ.get("MYSQL_PORT", "3306")))
parser.add_argument("--db-user", default=os.environ.get("MYSQL_USER", "root"))
parser.add_argument("--db-password", default=os.environ.get("MYSQL_PASSWORD", ""))
parser.add_argument("--db-name", default=os.environ.get("MYSQL_DB", "opensourse_project_team6"))
parser.add_argument("--sqlite", default=os.environ.get("NUTRITION_DB", "Frontend\app\src\main\assets\nutrition_data.db"))
args = parser.parse_args()

print(f"[INFO] SQLite 파일 경로: {args.sqlite}")

# Check if SQLite file exists
if not os.path.isfile(args.sqlite):
    print(f"[ERROR] SQLite 파일이 존재하지 않습니다: {args.sqlite}")
    exit(1)

# Connect to SQLite
try:
    sq_conn = sqlite3.connect(args.sqlite)
    sq_cur = sq_conn.cursor()
    print("[INFO] SQLite 연결 성공")
except Exception as e:
    print("[ERROR] SQLite 연결 실패:", e)
    exit(1)

# Connect to MySQL
try:
    my_conn = pymysql.connect(
        host=args.db_host,
        port=args.db_port,
        user=args.db_user,
        password=args.db_password,
        db=args.db_name,
        charset='utf8'
    )
    my_cur = my_conn.cursor()
    print("[INFO] MySQL 연결 성공")
except Exception as e:
    print("[ERROR] MySQL 연결 실패:", e)
    exit(1)

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
try:
    my_cur.execute(create_sql)
    print("[INFO] food 테이블 생성 또는 확인 완료")
except Exception as e:
    print("[ERROR] food 테이블 생성 실패:", e)
    exit(1)

# Load data from SQLite and insert
try:
    sq_cur.execute('SELECT * FROM nutrition')
    rows = sq_cur.fetchall()
    print(f"[INFO] SQLite에서 {len(rows)}개의 행 불러옴")
except Exception as e:
    print("[ERROR] nutrition 테이블 조회 실패:", e)
    exit(1)

insert_sql = """
INSERT INTO food (name, category, rep_name, subcategory, standard_amount, energy, protein, fat, carbohydrate, sugar, sodium, cholesterol, saturated_fat)
VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
"""

inserted = 0
for idx, row in enumerate(rows):
    values = row[:13]  # image_url 빠졌을 경우를 대비해 조정 가능
    try:
        my_cur.execute(insert_sql, values)
        inserted += 1
    except Exception as e:
        print(f"[ERROR] row {idx} 삽입 실패. 값: {values}")
        print("원인:", e)

my_conn.commit()
print(f"[SUCCESS] 총 {inserted}개의 행이 food 테이블에 삽입되었습니다.")
