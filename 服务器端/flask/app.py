import tools
from flask import Flask, request, jsonify
import json
from database import *
app = Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def hello_world():
    return 'hello world!'


@app.route('/auction/Register/', methods=['POST'])
def Register_data():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data = db_Register(req_data)
    # flask已经为json准备了专门的模块：jsonify。jsonify不仅会将内容转换为json，而且也会修改Content-Type为application/json。
    return jsonify(res_data)


@app.route('/auction/Login/', methods=['POST'])
def Login_data():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data = db_Login(req_data)
    # flask已经为json准备了专门的模块：jsonify。jsonify不仅会将内容转换为json，而且也会修改Content-Type为application/json。
    return jsonify(res_data)


@app.route('/auction/Auction_info/', methods=['GET', 'POST'])
def CreateAuction_data():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    tools.CreateAuction(req_data)
    res_data = db_CreateAuction(req_data)
    return jsonify(res_data)


@app.route('/auction/Auction_CommodityDescription/', methods=['GET', 'POST'])
def CommodityDescription_data():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    tools.CommodityDescription(req_data)
    res_data = dict()
    res_data['flag'] = '上传成功！'
    return jsonify(res_data)


@app.route('/auction/Auction_getCount/', methods=['GET', 'POST'])
def getCount_data():
    count1, count2 = db_count()
    res_data = dict()
    res_data['count1'] = str(count1)
    res_data['count2'] = str(count2)
    return jsonify(res_data)


@app.route('/auction/getAuction/<args>', methods=['POST'])
def getAuction_date(args):
    res_data = db_getAuction(args)
    print(res_data)
    return jsonify(res_data)


@app.route('/auction/SgetAuction/<args>', methods=['POST'])
def SgetAuction_date(args):
    res_data = db_SgetAuction(args)
    print(res_data)
    return jsonify(res_data)

#查询拍品订单
@app.route('/auction/QueryCommodityOrder/', methods=['POST'])
def QueryCommodityOrder():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data=db_QueryCommodityOrder(req_data['commodity_id'])
    return jsonify(res_data)

#查询保证金
@app.route('/auction/QueryBZJ/<args>', methods=['POST'])
def QueryBZJ(args):
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data = dict()

    if args=='Q':
        if db_QueryBZJ(req_data)==True:
            res_data['flag']='1'
        else:
            res_data['flag']='0'
    elif args=='I':
        res_data['errmsg']=db_InsertBZJ(req_data)
    return jsonify(res_data)

#插入拍卖记录
@app.route('/auction/InsertAct/', methods=['POST'])
def InsertAct():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data=db_InsertAct(req_data)
    print(res_data)
    return jsonify(res_data)

#查询用户信息
@app.route('/auction/QueryUserInfo/', methods=['POST'])
def QueryUserInfo():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data = db_QueryUserInfo(req_data)
    return jsonify(res_data[0])


#更新用户信息
@app.route('/auction/UpdateUserInfo/', methods=['POST'])
def UpdateUserInfo():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data = dict()
    res_data['errmsg']=db_UpdateUserInfo(req_data)
    print(res_data)
    return jsonify(res_data)


#查询用户订单
@app.route('/auction/QueryUSerOrder/', methods=['POST'])
def QueryUSerOrder():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data=db_QueryUSerOrder(req_data)
    return jsonify(res_data)

#用户订单支付
@app.route('/auction/ZHIFU/', methods=['POST'])
def ZHIFU():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data=dict()
    res_data['flag']=db_ZHIFU(req_data)
    return jsonify(res_data)


#获取公告
@app.route('/auction/GetNotice/', methods=['POST'])
def GetNotice():
    res_data=db_GetNotice()
    return jsonify(res_data)

#获取成交订单
@app.route('/auction/GetSuccessOrder/', methods=['POST'])
def GetSuccessOrder():
    res_data=db_GetSuccessOrder()
    return jsonify(res_data)

#获取流拍订单
@app.route('/auction/GetUnsuccessfulOrder/', methods=['POST'])
def GetUnsuccessfulOrder():
    res_data=db_GetUnsuccessfulOrder()
    print(res_data)
    return jsonify(res_data)

#统计
@app.route('/auction/Statistics/', methods=['POST'])
def Statistics():
    #生成饼图
    tools.Pie()
    #总计
    res_data=tools.total()
    return jsonify(res_data)

#用户管理功能
@app.route('/auction/UserManagement/', methods=['POST'])
def UserManagement():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    print(req_data)
    res_data=dict()
    res_data['errmsg']=db_UserManagement(req_data)
    return jsonify(res_data)

#删除用户
@app.route('/auction/DiscontinueUser/<args>', methods=['POST'])
def DiscontinueUser(args):
    res_data=dict()
    res_data['errmsg']=db_DiscontinueUser(args)
    return jsonify(res_data)
#恢复用户
@app.route('/auction/RecoveryUser/<args>', methods=['POST'])
def RecoveryUser(args):
    res_data=dict()
    res_data['errmsg']=db_RecoveryUser(args)
    return jsonify(res_data)

#用户参拍记录

@app.route('/auction/GetUserRecord/', methods=['POST'])
def GetUserRecord():
    req_data = request.get_data()
    req_data = json.loads(req_data)  # 将json转换为字典
    res_data=db_GetUserRecord(req_data['user_id'])
    print(res_data)
    return jsonify(res_data)
if __name__ == '__main__':

    app.run()