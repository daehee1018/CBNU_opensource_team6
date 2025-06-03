import sqlite3
import os

def init_db():
    # 데이터베이스 파일이 저장될 디렉토리 생성
    if not os.path.exists('instance'):
        os.makedirs('instance')
    
    # 데이터베이스 연결
    conn = sqlite3.connect('instance/nutrition.db')
    cursor = conn.cursor()
    
    try:
        # schema.sql 파일 읽기
        with open('schema.sql', 'r') as f:
            schema = f.read()
        
        # 테이블 생성
        cursor.executescript(schema)
        
        # 변경사항 저장
        conn.commit()
        print("Database initialized successfully!")
        
    except Exception as e:
        print(f"Error initializing database: {e}")
        
    finally:
        # 연결 종료
        conn.close()

if __name__ == '__main__':
    init_db() 