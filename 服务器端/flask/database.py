# import pymysql
#
# # 打开数据库连接
# db = pymysql.connect("localhost","root","123456","Auction" )
#
# # 使用 cursor() 方法创建一个游标对象 cursor
# cursor = db.cursor()
#
# # 使用 execute()  方法执行 SQL 查询
# cursor.execute("SELECT VERSION()")
#
# # 使用 fetchone() 方法获取单条数据.
# data = cursor.fetchone()
#
# print("Database version : %s " % data)
#
# # 关闭数据库连接
# db.close()
import pymysql
import pandas as pd


def db_Register(req_data):
    # 打开数据库连接
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        sql_1 = 'insert into user(user_id,user_name,pwd,admin) values(\'' + req_data['user_id'] + '\',\'' + req_data[
            'user_name'] + '\',\'' + req_data['pwd'] + '\',%d)' % req_data['admin']
        sql_2 = 'insert into user_info(user_id,phone,name,address) values(\'' + req_data['user_id'] + '\',\'' + \
                req_data[
                    'phone'] + '\',\'' + req_data['name'] + '\',\'' + req_data['address'] + '\')'
        # 这里注意连续用了两处 with 好处就在于 with 结束后会自动 close cursor 而免去了 cursor.close()
        with connection.cursor() as cursor:
            cursor.execute(sql_1)
            cursor.execute(sql_2)
        # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        flag = '1'
        errmsg = '注册成功！'
    except:
        flag = '0'
        sql = 'select user_name from user where user_name=\'%s\'' % req_data['user_name']
        data = pd.read_sql(sql=sql, con=connection)
        if (len(data.values) != 0):
            print(data.values)
            errmsg = '用户名%s被占用，请换个用户名再试！' % data.values[0][0]
        else:
            errmsg = '注册失败，请联系管理员！'
    finally:
        connection.close()
    res_data = dict()
    res_data['flag'] = flag
    res_data['errmsg'] = errmsg
    return res_data


def db_Login(req_data):
    # 打开数据库连接
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    res_data = dict()
    try:
        sql = 'select user_name from user where user_name=\'%s\'' % req_data['user_name']
        data = pd.read_sql(sql=sql, con=connection)
        if (len(data.values) != 0):
            sql = 'select pwd from user where user_name=\'%s\'' % req_data['user_name']
            data = pd.read_sql(sql=sql, con=connection)
            if (data.values[0][0] == req_data['pwd']):
                flag = '1'
                errmsg = '登录成功！'
                sql = 'select user_id from user where user_name=\'%s\'' % req_data['user_name']
                res_data['user_id'] = pd.read_sql(sql=sql, con=connection).values[0][0]
                sql = 'select admin from user where user_name=\'%s\'' % req_data['user_name']
                res_data['admin'] = str(pd.read_sql(sql=sql, con=connection).values[0][0])

            else:
                flag = '0'
                errmsg = '密码错误！'
        else:
            flag = '0'
            errmsg = '无此用户名，请注册！'
    except:
        flag = '0'
        errmsg = '登录失败！内部错误，请联系管理员！'

    finally:
        connection.close()
    res_data['flag'] = flag
    res_data['errmsg'] = errmsg
    return res_data


def db_CreateAuction(req_data):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    Id = req_data['Id']
    title = req_data['title']
    start_price = req_data['start_price']
    Margin = req_data['Margin']
    end_time = req_data['end_time']
    Date = req_data['Date']
    res_data = dict()
    try:
        with connection.cursor() as cursor:
            sql = 'insert into commodity(commodity_id,commodity_name,start_price,margin,end_time,start_time) values(\'' + Id + '\',\'' + title + '\',' + start_price + ',' + Margin + ',\'' + end_time + '\',\'' + Date + '\')'
            cursor.execute(sql)
        # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        errmsg = '上传成功！'
    except:
        errmsg = '提交失败，请重新提交!'

    finally:
        connection.close()

    res_data['flag'] = errmsg

    return res_data


def db_count():
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        sql1 = 'select count(*) from commodity where now() < start_time'
        count1 = pd.read_sql(sql=sql1, con=connection).values[0][0]
        sql2 = 'select count(*) from commodity where now() > start_time and now() < end_time'
        count2 = pd.read_sql(sql=sql2, con=connection).values[0][0]
    finally:
        connection.close()
    return count1, count2


def db_getAuction(args):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        if args == "1":
            sql = 'select * from commodity where now() > start_time and now() < end_time'
            data = pd.read_sql(sql=sql, con=connection)
            data['is_cd'] = '1'
        elif args == "2":
            sql = 'select * from commodity where now() < start_time'
            data = pd.read_sql(sql=sql, con=connection)
            data['is_cd'] = '2'
        elif args == "3":
            sql1 = 'select * from commodity where now() > start_time and now() < end_time'
            data1 = pd.read_sql(sql=sql1, con=connection)
            data1['is_cd'] = '1'
            sql2 = 'select * from commodity where now() < start_time'
            data2 = pd.read_sql(sql=sql2, con=connection)
            data2['is_cd'] = '2'
            data = data1.append(data2)
            # 重置index
            data = data.reset_index(drop=True)
            data_ = data.to_dict(orient='index')
            data = data.drop(['start_time'], axis=1)
            data = data.drop(['end_time'], axis=1)
            # data = data.drop(['max_user_id'], axis=1)
            data = data.to_dict(orient='index')
            import tools
            for i in range(len(data_)):
                if (data_[i]['is_cd'] == '1'):
                    data[i]['time'] = tools.LenTime(data_[i]['end_time'], False)
                    sql = 'select * from commodity_order where commodity_id = \'' + data_[i][
                        'commodity_id'] + '\'' + ' order by id desc'
                    D = pd.read_sql(sql=sql, con=connection)
                    if (len(D.to_dict(orient='index')) > 0):
                        data[i]['start_price'] = D.to_dict(orient='index')[0]['price']
                elif (data_[i]['is_cd'] == '2'):
                    data[i]['time'] = tools.LenTime(data_[i]['start_time'], True)
            return data
        else:
            return {}

    finally:
        connection.close()
    # 删除不需要的列
    data_ = data.to_dict(orient='index')
    data = data.drop(['start_time'], axis=1)
    data = data.drop(['end_time'], axis=1)
    # data = data.drop(['max_user_id'], axis=1)
    data = data.to_dict(orient='index')
    import tools
    for i in range(len(data_)):
        if (data_[i]['is_cd'] == '1'):
            data[i]['time'] = tools.LenTime(data_[i]['end_time'], False)
            connection = pymysql.connect("localhost", "root", "123456", "Auction")
            sql = 'select * from commodity_order where commodity_id = \'' + data_[i][
                'commodity_id'] + '\'' + ' order by id desc'
            D = pd.read_sql(sql=sql, con=connection)
            connection.close()
            if (len(D.to_dict(orient='index')) > 0):
                data[i]['start_price'] = D.to_dict(orient='index')[0]['price']
        elif (data_[i]['is_cd'] == '2'):
            data[i]['time'] = tools.LenTime(data_[i]['start_time'], True)
    return data


def db_SgetAuction(args):
    # 切片，例如args==1+s，str=s，args=1
    str = args.split('+')[1]
    args = args.split('+')[0]
    num = 0
    s = ""
    # 如果str有空格，继续切片，s为模糊查询，加下面的sql语句中
    for i in str.split():
        num = num + 1
        if (num > 1):
            s = s + ' or '
        s = s + 'commodity_name like ' + '\'' + "%" + i + "%" + '\''
    # 数据库连接
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    # 根据args的参数，args为CheckBox的状态标准，进行sql查询，使用pandas库的read_sql进行查询，获得查询结果
    try:
        if args == "1":
            sql = 'select * from commodity where now() > start_time and now() < end_time and (' + s + ')'
            data = pd.read_sql(sql=sql, con=connection)
            data['is_cd'] = '1'
        elif args == "2":
            sql = 'select * from commodity where now() < start_time and (' + s + ')'
            data = pd.read_sql(sql=sql, con=connection)
            data['is_cd'] = '2'
        elif args == "3":
            sql1 = 'select * from commodity where now() > start_time and now() < end_time and (' + s + ')'
            data1 = pd.read_sql(sql=sql1, con=connection)
            data1['is_cd'] = '1'
            sql2 = 'select * from commodity where now() < start_time and (' + s + ')'
            data2 = pd.read_sql(sql=sql2, con=connection)
            data2['is_cd'] = '2'
            # 将两个查询结果合并
            data = data1.append(data2)
            # 重置index
            data = data.reset_index(drop=True)
            data_ = data.to_dict(orient='index')
            data = data.drop(['start_time'], axis=1)
            data = data.drop(['end_time'], axis=1)
            # 将data转为字典
            data = data.to_dict(orient='index')
            import tools
            for i in range(len(data_)):
                if (data_[i]['is_cd'] == '1'):
                    data[i]['time'] = tools.LenTime(data_[i]['end_time'], False)
                    sql = 'select * from commodity_order where commodity_id = \'' + data_[i][
                        'commodity_id'] + '\'' + ' order by id desc'
                    D = pd.read_sql(sql=sql, con=connection)
                    if (len(D.to_dict(orient='index')) > 0):
                        data[i]['start_price'] = D.to_dict(orient='index')[0]['price']
                elif (data_[i]['is_cd'] == '2'):
                    data[i]['time'] = tools.LenTime(data_[i]['start_time'], True)
            return data
        else:
            return {}

    finally:
        connection.close()

    # 删除不需要的列
    data_ = data.to_dict(orient='index')
    data = data.drop(['start_time'], axis=1)
    data = data.drop(['end_time'], axis=1)
    data = data.to_dict(orient='index')
    import tools
    for i in range(len(data_)):
        if (data_[i]['is_cd'] == '1'):
            data[i]['time'] = tools.LenTime(data_[i]['end_time'], False)
            connection = pymysql.connect("localhost", "root", "123456", "Auction")
            sql = 'select * from commodity_order where commodity_id = \'' + data_[i][
                'commodity_id'] + '\'' + ' order by id desc'
            D = pd.read_sql(sql=sql, con=connection)
            connection.close()
            if (len(D.to_dict(orient='index')) > 0):
                data[i]['start_price'] = D.to_dict(orient='index')[0]['price']
        elif (data_[i]['is_cd'] == '2'):
            data[i]['time'] = tools.LenTime(data_[i]['start_time'], True)
    return data


def db_QueryCommodityOrder(args):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        sql = 'select * from commodity_order where commodity_id = \'' + args + '\'' + ' order by id desc'
        data = pd.read_sql(sql=sql, con=connection)
        sql = 'select * from commodity where commodity_id=\'' + args + '\''
        data1 = pd.read_sql(sql=sql, con=connection)
    finally:
        connection.close()
    import datetime
    data = data.to_dict(orient='index')
    for i in range(len(data)):
        data[i]['time']=data[i]['time'].strftime('%Y-%m-%d %H:%M:%S')
    end_time = data1.to_dict(orient='index')[0]['end_time']
    start_time = data1.to_dict(orient='index')[0]['start_time']
    now_time = datetime.datetime.now()
    if (end_time > now_time):
        days = (end_time - now_time).days
        sec = (end_time - now_time).seconds + days * 24 * 60 * 60
        data['time'] = int(sec)
    else:
        data['time'] = 0
    if (now_time > start_time):
        data['start_time'] = 0
    else:
        days = (start_time - now_time).days
        sec = (start_time - now_time).seconds + days * 24 * 60 * 60
        data['start_time'] = int(sec)
    print(data)
    return data


def db_QueryBZJ(args):
    commodity_id = args['commodity_id']
    user_id = args['user_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select user_id from bzj where commodity_id = \'' + commodity_id + '\''
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()
    if len(data.to_dict(orient='index')) >= 1:
        for i in range(len(data.to_dict(orient='index'))):
            if (data.to_dict(orient='index')[i]['user_id'] == user_id):
                return True
        return False
    else:
        return False


def db_InsertBZJ(args):
    commodity_id = args['commodity_id']
    user_id = args['user_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        with connection.cursor() as cursor:
            sql = 'insert into bzj(commodity_id,user_id) values(\'' + commodity_id + '\',' + '\'' + user_id + '\')'
            cursor.execute(sql)
        # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        errmsg = '1'
    except:
        errmsg = '0'
    finally:
        connection.close()
    return errmsg


def db_InsertAct(args):
    commodity_id = args['commodity_id']
    id = args['id']
    user_name = args['user_name']
    price = args['price']
    time = args['time']
    user_id = args['user_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    res_data = dict()
    try:
        with connection.cursor() as cursor:
            sql = 'insert into commodity_order(commodity_id,id,user_name,price,time,user_id) values(\'' + commodity_id + '\',' + str(
                id) + ',\'' + user_name + '\',' + str(price) + ',\'' + time + '\',\'' + user_id + '\')'
            cursor.execute(sql)
        # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        errmsg = '参拍成功！'
    except:
        errmsg = '参拍失败，请重新提交!'

    finally:
        connection.close()

    res_data['flag'] = errmsg

    return res_data


def db_QueryUserInfo(args):
    user_id = args['user_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from user_info where user_id = \'' + user_id + '\''
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()

    return data.to_dict(orient='index')


def db_UpdateUserInfo(args):
    user_id = args['user_id']
    phone = args['phone']
    name = args['name']
    address = args['address']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        with connection.cursor() as cursor:
            sql = 'UPDATE user_info SET phone = \'' + phone + '\', name = \'' + name + '\', address = \'' + address + '\' WHERE user_id = \'' + user_id + '\''
            cursor.execute(sql)
            # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        errmsg = '修改成功！'
    except:
        errmsg = '修改失败，请重新提交!'

    finally:
        connection.close()

    return errmsg


def db_QueryUSerOrder(args):
    user_id = args['user_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from user_order where user_id = \'' + user_id + '\''
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()

    if len(data.to_dict(orient='index')) >= 1:
        res_data = data.to_dict(orient='index')
        for i in range(len(res_data)):
            commodity_id = res_data[i]['commodity_id']
            connection = pymysql.connect("localhost", "root", "123456", "Auction")
            try:

                sql = 'select * from commodity where commodity_id = \'' + commodity_id + '\''
                data1 = pd.read_sql(sql=sql, con=connection)


            finally:
                connection.close()
            res_data[i]['commodity_name'] = data1.to_dict(orient='index')[0]['commodity_name']
            res_data[i]['margin'] = data1.to_dict(orient='index')[0]['margin']
            return res_data
    else:
        return data.to_dict(orient='index')


def db_ZHIFU(args):
    user_id = args['user_id']
    commodity_id = args['commodity_id']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        with connection.cursor() as cursor:
            sql = 'UPDATE user_order SET pay = ' + str(
                1) + ' WHERE user_id = \'' + user_id + '\' and commodity_id = \'' + commodity_id + '\''
            cursor.execute(sql)
            # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
        connection.commit()
        errmsg = '支付成功！'
    except:
        errmsg = '支付失败，请重新提交!'

    finally:
        connection.close()

    return errmsg


def db_GetNotice():
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from notice order by time desc'
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()
    data=data.to_dict(orient='index')
    for i in range(len(data)):
        data[i]['time']=data[i]['time'].strftime('%Y-%m-%d %H:%M:%S')

    return data


def db_GetUserName(args):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select user_name from user where user_id =\'' + args + '\''
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()

    return data.to_dict(orient='index')


def db_GetCommodityName(args):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select commodity_name from commodity where commodity_id =\'' + args + '\''
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()

    return data.to_dict(orient='index')


def db_GetSuccessOrder():
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from user_order '
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()
    list = []
    tmp = data.to_dict(orient='index')
    dic = dict()
    for i in range(len(tmp)):
        dic['pay'] = tmp[i]['pay']
        dic['user_name'] = db_GetUserName(tmp[i]['user_id'])[0]['user_name']
        dic['commodity_name'] = db_GetCommodityName(tmp[i]['commodity_id'])[0]['commodity_name']
        dic['commodity_id'] = tmp[i]['commodity_id']
        dic['price'] = tmp[i]['price']
        list.append(dic.copy())
        # 列表中存放字典遇到的问题 需要加copy()
        dic.clear()
    return pd.DataFrame(list).to_dict(orient='index')


def db_GetUnsuccessfulOrder():
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from unsuccessful_order '
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()
    tmp = data.to_dict(orient='index')
    for i in range(len(tmp)):
        tmp[i]['time'] = str(tmp[i]['time'])
    return tmp


def db_UserManagement(args):
    user_name = args['user_name']
    pwd = args['pwd']
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql = 'select * from user where user_name = \'' + user_name + '\''
    data = pd.read_sql(sql=sql, con=connection)
    if (len(data) == 0):
        connection.close()
        return '无此用户名！'
    else:
        try:
            with connection.cursor() as cursor:
                sql = 'UPDATE user SET pwd = \'' + pwd + '\' WHERE user_name = \'' + user_name + '\''
                cursor.execute(sql)
                # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
            connection.commit()
            errmsg = '修改成功！'

        finally:
            connection.close()

        return errmsg


def db_DiscontinueUser(args):
    user_name = args
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql = 'select * from user where user_name = \'' + user_name + '\''
    data = pd.read_sql(sql=sql, con=connection)
    if (len(data) == 0):
        connection.close()
        return '无此用户名！'
    else:
        try:
            with connection.cursor() as cursor:
                sql = 'UPDATE user SET admin = \'' + str(2) + '\' WHERE user_name = \'' + user_name + '\''
                cursor.execute(sql)
                # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
            connection.commit()
            errmsg = '停用成功！'

        finally:
            connection.close()

        return errmsg


def db_RecoveryUser(args):
    user_name = args
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    sql = 'select * from user where user_name = \'' + user_name + '\''
    data = pd.read_sql(sql=sql, con=connection)
    if (len(data) == 0):
        connection.close()
        return '无此用户名！'
    else:
        try:
            with connection.cursor() as cursor:
                sql = 'UPDATE user SET admin = \'' + str(0) + '\' WHERE user_name = \'' + user_name + '\''
                cursor.execute(sql)
                # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
            connection.commit()
            errmsg = '恢复成功！'

        finally:
            connection.close()

        return errmsg


def db_GetUserRecord(args):
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:
        sql = 'select commodity_id from bzj where user_id = \'' + args + '\''
        data = pd.read_sql(sql=sql, con=connection)
        # commodity_id的dict
        C_ID = data.to_dict(orient='index')
        list = []
        dic = dict()
        for i in range(len(C_ID)):
            commodity_id = C_ID[i]['commodity_id']
            sql = 'select * from commodity_order where commodity_id = \'' + commodity_id + '\'' + ' order by id desc'
            data = pd.read_sql(sql=sql, con=connection)
            price = data.to_dict(orient='index')[0]['price']
            # 竞价最高的userid
            First_user_id = data.to_dict(orient='index')[0]['user_id']
            sql = 'select * from commodity where commodity_id=\'' + commodity_id + '\''
            data = pd.read_sql(sql=sql, con=connection)
            start_time = data.to_dict(orient='index')[0]['start_time']
            end_time = data.to_dict(orient='index')[0]['end_time']
            commodity_name = data.to_dict(orient='index')[0]['commodity_name']
            margin = data.to_dict(orient='index')[0]['margin']
            dic['commodity_id'] = commodity_id
            dic['commodity_name'] = commodity_name
            dic['end_time'] = end_time.strftime('%Y-%m-%d %H:%M:%S')
            dic['price'] = price
            dic['margin'] = margin
            import datetime
            now_time = datetime.datetime.now()
            # 三种状态 1. 拍卖中 2.成功拍得 3.未拍得
            if (now_time >= start_time and now_time <= end_time):
                dic['flag'] = 1
            elif (now_time > end_time):
                if (args == First_user_id):
                    dic['flag'] = 2
                else:
                    dic['flag'] = 3
            list.append(dic.copy())
            dic.clear()
    finally:
        connection.close()
    return pd.DataFrame(list).to_dict(orient='index')
