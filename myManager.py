from flask import Flask
from flask_restful import reqparse, Api, Resource
import requests, json
from time import time, sleep
from getpass import getpass

app = Flask(__name__)
api = Api(app)


class cycCalculator(Resource):
    def __init__(self):
        self.manger = m
        self.reqparser = reqparse.RequestParser()

        self.reqparser.add_argument('commit', type=str, location='json')  # Repeat for multiple variables
        self.reqparser.add_argument('complexity', type=str, location='json')

    def get(self):
        if self.manger.workerNumNow < self.manger.numWorkers:
            sleep(0.1)
            return {'sha': -2}
        if len(self.manger.commits) == 0:
            return {'sha': -1}
        commitSha = self.manger.commits[0]
        del self.manger.commits[0]
        print(commitSha)
        return {'sha': commitSha}

    def post(self):
        print(self.reqparser.parse_args())
        args = self.reqparser.parse_args()
        self.manger.commitsComplexity.append({'sha': args['commit'], 'complexity': args['complexity']})
        if len(self.manger.commitsComplexity) == self.manger.commitsNum:
            endTime = time() - self.manger.startTime
            totalComplexity = 0
            for x in self.manger.commitsComplexity:
                xcomp = float(x['complexity'])
                if xcomp > 0:
                    totalComplexity += xcomp
                else:
                    print("Commit {} has no computable files".format(x['sha']))
            averageComplexity = totalComplexity / len(self.manger.commitsComplexity)
            print("\n\nAverage cyclomatic complexity for the repository ({}/{}) is: {}".format(self.manger.owner,
                                                                                               self.manger.repo,
                                                                                               averageComplexity))
            print("{} workers finished work in {} seconds\n\n".format(self.manger.numWorkers, endTime))
        return {'success': True}


class isReady(Resource):
    def __init__(self):
        self.ser = m

    def get(self):
        self.ser.workerNumNow +=1
    def post(self):
        pass

class initServer(Resource):
    def __init__(self):
        self.server=m
        self.reqparser = reqparse.RequestParser()

        self.reqparser.add_argument('gitid', type=str, location='json')
        self.reqparser.add_argument('pwd', type=str, location='json')
        self.reqparser.add_argument('owner', type=str, location='json')
        self.reqparser.add_argument('repo', type=str, location='json')
        self.reqparser.add_argument('worknum', type=int, location='json')

    def get(self):
        if(self.server.isInit):
            return {'isInit':self.server.isInit,'repo':self.server.repo}
        return {'isInit':self.server.isInit}

    def post(self):
        args = self.reqparser.parse_args()
        self.server.numWorkers,gitid,pwd,owner,repo=args['worknum'],args['gitid'],args['pwd'],args['owner'],args['repo']
        r = requests.get(
            "https://api.github.com/repos/{}/{}/commits?page={}&per_page=1".format(owner, repo, 1),
            auth=(gitid, pwd))
        re = json.loads(r.text)
        if(len(re)!=1):
            owner = "python"
            repo = "core-workflow"
        else:
            self.server.commits.append(re[0]['sha'])
        page=2
        while(True):
            res = requests.get(
                "https://api.github.com/repos/{}/{}/commits?page={}&per_page=1".format(owner, repo, page),
                auth=(gitid, pwd))
            rese = json.loads(res.text)
            if(len(rese)!=1):
                break
            self.server.commits.append(re[0]['sha'])
            page+=1
        self.server.isInit=True
        self.server.repo="https://github.com/{}/{}".format(owner, repo)
        return {'isReady':True,'repo':self.server.repo}


api.add_resource(cycCalculator, '/cyc', endpoint="cyc")
api.add_resource(isReady, "/isReady", endpoint="isReady")
api.add_resource(initServer,'/initServer',endpoint='initServer')

class manager():
    def __init__(self):
        self.numWorkers = 0 #work sum
        self.workerNumNow = 0 # the number of work online
        self.startTime = 0.0
        self.commits = [] #array of versions
        self.commitsComplexity = []
        self.commitNum = 0 #the task completed by work
        self.isInit=False
        self.repo=''


if __name__ == '__main__':
    m = manager()
    app.run(port=5000)
