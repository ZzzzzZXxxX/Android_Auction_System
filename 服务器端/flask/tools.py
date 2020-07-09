import base64
import os
import pymysql


##处理商品详情的存储


def CommodityDescription(req_data):
    num = req_data['num']
    isExists = os.path.exists('static/' + req_data['Id'] + '/')
    if not isExists:
        os.makedirs('static/' + req_data['Id'] + '/')
    if num > 0:
        for i in range(num):
            img = base64.b64decode(req_data['img%d' % (i + 1)])
            with open('static/' + req_data['Id'] + '/img%d.png' % (i + 1), 'wb') as fp:
                fp.write(img)
    with open('static/' + req_data['Id'] + '/Description.html', 'w', encoding="utf-8") as fp:
        fp.write(req_data['Htm'])
    with open('static/' + req_data['Id'] + '/json.log', 'w', encoding="utf-8") as fp:
        fp.write(str(req_data))


##处理创建商品的图片存储
def CreateAuction(req_data):
    isExists = os.path.exists('static/' + req_data['Id'] + '/')
    if not isExists:
        os.makedirs('static/' + req_data['Id'] + '/')
    img = base64.b64decode(req_data['img'])
    with open('static/' + req_data['Id'] + '/img.png', 'wb') as fp:
        fp.write(img)
    with open('static/' + req_data['Id'] + '/json.log', 'w', encoding="utf-8") as fp:
        fp.write(str(req_data))


##处理拍卖成功与流拍
def UserOrder():
    import pymysql
    import pandas as pd
    import time
    import uuid
    print("拍卖开始监控中！！！")

    while True:
        # 流拍
        list = []
        # 成交
        list1 = []
        # 连接数据库
        connection = pymysql.connect("localhost", "root", "123456", "Auction")
        sql = 'select * from commodity where now() >= end_time'
        # 查询已经结束的拍品
        data = pd.read_sql(sql=sql, con=connection)
        sql = 'select * from user_order'
        # 查询用户订单表
        data1 = pd.read_sql(sql=sql, con=connection)
        temp1 = data1.to_dict(orient='index')
        # 将用户订单表中的commodity_id加入list1中
        for i in range(len(temp1)):
            list1.append(temp1[i]['commodity_id'])
        sql = 'select * from unsuccessful_order'
        data2 = pd.read_sql(sql=sql, con=connection)
        tmp = data2.to_dict(orient='index')
        # 将流拍表中的commodity_id加入list中
        for i in range(len(tmp)):
            list.append(tmp[i]['commodity_id'])
        # 将已经结束拍买的拍品的数据转为字典
        temp = data.to_dict(orient='index')
        for i in range(len(temp)):
            # 经结束拍买的拍品数据里的commodity_id不在流拍list也不在成交的list里
            if temp[i]['commodity_id'] not in list1 and temp[i]['commodity_id'] not in list:
                # 倒序查询流拍的拍品在拍品拍卖记录订单表中的数据
                sql = 'select * from commodity_order where commodity_id = \'' + temp[i][
                    'commodity_id'] + '\'' + ' order by id desc'
                data = pd.read_sql(sql=sql, con=connection)
                # 转为字典
                temp2 = data.to_dict(orient='index')
                # 结果大于等于1
                if len(temp2) >= 1:
                    try:
                        # 将出价最高的userid和price插入用户订单表
                        with connection.cursor() as cursor:
                            sql = 'insert into user_order(order_id,commodity_id,user_id,price,pay) values(\'' + str(
                                uuid.uuid1()) + '\',\'' + temp2[0]['commodity_id'] + '\',\'' + temp2[0][
                                      'user_id'] + '\',' + str(temp2[0]['price']) + ',0)'
                            cursor.execute(sql)

                        connection.commit()
                        # 成交公告
                        notice(1, temp[i]['commodity_name'], temp2[0]['user_name'])
                    except:
                        print("插入用户订单表出错")
                    print(temp[i]['commodity_name'] + '-->由'+temp2[0]['user_name']+'拍得')
                else:
                    try:
                        # 将出价最高的userid和price插入流拍表
                        with connection.cursor() as cursor:
                            sql = 'insert into unsuccessful_order(commodity_id,commodity_name,time) values(\'' + \
                                  temp[i][
                                      'commodity_id'] + '\',\'' + temp[i][
                                      'commodity_name'] + '\'' + ',now())'
                            cursor.execute(sql)
                        connection.commit()
                        # 流拍公告
                        notice(0, temp[i]['commodity_name'], '')
                    except:
                        print("插入流拍表出错")
                    print(temp[i]['commodity_name'] + "-->流拍")
        # 断开数据库
        connection.close()
        # 延时5秒
        time.sleep(2)


##处理公告信息
def notice(flag, commodity='', user=''):
    if (flag == 1):
        connection = pymysql.connect("localhost", "root", "123456", "Auction")
        try:
            text = '成交公告：' + commodity + '由' + user + '拍得'
            with connection.cursor() as cursor:
                sql = 'insert into notice(title,content,time) values(\'' + text + '\',\'' + text + '\',now())'
                cursor.execute(sql)
            # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
            connection.commit()
        except:
            print('notice bug')

        finally:
            connection.close()
    else:
        connection = pymysql.connect("localhost", "root", "123456", "Auction")
        try:
            text = '流拍公告：' + commodity + '流拍'
            with connection.cursor() as cursor:
                sql = 'insert into notice(title,content,time) values(\'' + text + '\',\'' + text + '\',now())'
                cursor.execute(sql)
            # 连接完数据库并不会自动提交，所以需要手动 commit 你的改动
            connection.commit()
        except:
            print('notice bug')

        finally:
            connection.close()


# 统计饼图
def Pie():
    import pandas as pd
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select * from user_order '
        data = pd.read_sql(sql=sql, con=connection)
        sql = 'select * from unsuccessful_order '
        data1 = pd.read_sql(sql=sql, con=connection)

    finally:
        connection.close()
    x_data = ['成功拍出', '流拍']
    y_data = [len(data), len(data1)]
    from pyecharts.charts import Bar, Pie
    # 用于设值全局配置和系列配置（二者的区别已经在柱状图的博文中讲解过）
    from pyecharts import options as opts
    data_pair = [list(z) for z in zip(x_data, y_data)]
    c = (
        Pie(init_opts=opts.InitOpts(width="560px"))
            .add(series_name="成交分析", data_pair=data_pair)
            .set_global_opts(title_opts=opts.TitleOpts(title="成交统计"))
            .set_series_opts(label_opts=opts.LabelOpts(formatter="{b}: {c}"))
    )
    c.render('static/render.html')


# 统计流水
def total():
    import pandas as pd
    connection = pymysql.connect("localhost", "root", "123456", "Auction")
    try:

        sql = 'select sum(price) from user_order '
        data = pd.read_sql(sql=sql, con=connection)


    finally:
        connection.close()
    return data.to_dict('index')[0]


# 计算时间长度
def LenTime(t, start):
    import datetime
    now_time = datetime.datetime.now()
    if start == True:
        print(t)
        if t > now_time:
            days = (t - now_time).days
            sec = (t - now_time).seconds + days * 24 * 60 * 60
            return int(sec)
        else:
            return 0
    elif start == False:
        if t > now_time:
            days = (t - now_time).days
            sec = (t - now_time).seconds + days * 24 * 60 * 60
            return int(sec)
        else:
            return 0
