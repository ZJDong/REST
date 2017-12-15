import json, requests, subprocess
from getpass import getpass

def calculate(hashsha):
    bashCommand = "cd pulledRepo &" \
                  "git reset --hard {}".format(hashsha)
    command_output = subprocess.Popen(bashCommand, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.read().decode()
    print (command_output)
    command_output = subprocess.check_output(["radon", "cc", "-s", "-a", "pulledRepo"]).decode()
    print(command_output)
    r=''
    if command_output[command_output.rfind('(') + 1:-2] == "":
        print("NO RELEVENT FILES")
        r = requests.post("http://127.0.0.1:5000/cyc",
                          json={'commit': hashsha, 'complexity': -1})
    else:
        averageCC = float(command_output[command_output.rfind("(") + 1:-2].strip(')'))
        r = requests.post("http://127.0.0.1:5000/cyc",
                          json={'commit': hashsha, 'complexity': averageCC})
    print (r)

def initServer():
    workNum=int(input("How many workers do you want:"))
    authgithubid = input(
        "Input your github id and password to increse the github api limit.\nInput your github username:")
    authgithubpwd = getpass("Input your github password:")

    owner = input(
        "Press enter to use default repository: python/core-worlflow\n\nOr input the repository owner:")
    repo = input("Input the repository name:")
    r = requests.post("http://127.0.0.1:5000/initServer",
                      json={'gitid': authgithubid, 'pwd':authgithubpwd,'owner':owner,'repo':repo ,'worknum':workNum})
    print (r)

if __name__ == "__main__":
    isInit=False
    repoUrl=''
    while(isInit is False):
        r = requests.get("http://127.0.0.1:5000/initServer")
        json_data =  json.loads(r.text)
        isInit =json_data['isInit']
        if(isInit):
            repoUrl = json_data['repo']
            break
        initServer()
    bashCommand = "cd pulledRepo &" \
                  "rm -rf .git/ &" \
                  "git init &" \
                  "git remote add origin {} &" \
                  "git branch --set-upstream-to=origin/<branch> master & " \
                  "git pull".format(repoUrl)
    command_output = subprocess.Popen(bashCommand, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT).stdout.read().decode()
    print(command_output)
    print("Repository pulled completed")
    requests.get("http://127.0.0.1:5000/isReady")
    numDone = 0
    while True:
        r = requests.get("http://127.0.0.1:5000/cyc")
        json_data = json.loads(r.text)
        print(json_data)
        hashsha = json_data['sha']
        if hashsha == -2:
            print("Waiting for enough workers...")
        elif hashsha == -1:
            print("No commit left")
            break
        else:
            calculate(hashsha)
            numDone += 1
    print("\n\nCalculated the cyclomatic complexity for {} commits.\n\n".format(numDone))
