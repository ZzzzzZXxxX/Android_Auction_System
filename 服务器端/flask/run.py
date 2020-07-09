import threading
import tools
import os
#falsk启动
def job1():
    os.system('python app.py')
#监控拍卖启动
def job2():
    tools.UserOrder()
#线程1
thread1 = threading.Thread(target=job1, name='job1')
#线程1启动
thread1.start()
#线程2
thread2 = threading.Thread(target=job2, name='job2')
#线程2启动
thread2.start()