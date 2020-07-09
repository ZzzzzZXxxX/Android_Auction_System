import pymysql
import pandas as pd
import hashlib
import uuid
a=input("查询管理员用户请输入1，新增管理员用户输入2，删除管理员用户输入3\n")
if a=='1':
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql = 'select * from user where admin=1'
    data = pd.read_sql(sql=sql, con=connection)
    print(data.to_dict(orient='i1ndex'))
    connection.close()
elif a=='2':
    username=input("请输入管理员名：")
    pwd=input("请输入管理员密码：")
    m = hashlib.md5()
    m.update(pwd.encode('utf-8'))
    pwd=m.hexdigest()
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql_1 = 'insert into user(user_id,user_name,pwd,admin) values(\'' + str(uuid.uuid1()) + '\',\'' + username + '\',\'' + pwd + '\',1)'
    with connection.cursor() as cursor:
        cursor.execute(sql_1)
    connection.close()
    print('success')
elif a=='3':
    username=input('请输入删除的管理员用户名：')
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql='DELETE FROM user WHERE user_name =\''+username+'\''
    with connection.cursor() as cursor:
        cursor.execute(sql)
    connection.close()
    print('success')
else:
    print('请输入1、2、3')
