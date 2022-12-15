pipeline {
    agent none
    environment {
		DOCKERHUB_CREDENTIALS=credentials('dockerhub')
	}
    stages {
        stage('SCM Checkout') {
            agent any
            steps {
                // Get some code from a GitHub repository
                git 'https://github.com/omarmansor/PHP-application-master.git'
            }
        }
        stage('Push Ansible Configuration') {
            agent any
            steps {
                ansiblePlaybook credentialsId: 'Slave-Node', disableHostKeyChecking: true, forks: 5, installation: 'Ansible', inventory: 'ansible/hosts', playbook: 'ansible/test-server.yaml'
            }
        }

        stage('Deploy website') {
            agent {
                label 'Test-Server'
            }
            steps {
                
                git 'https://github.com/omarmansor/PHP-application-master.git'
                sh returnStatus: true, script: '''
                for PORT in 8080
                do
                  ID=$(\
                    docker container ls --format="{{.ID}}\t{{.Ports}}" |\
                    grep ${PORT} |\
                    awk '{print $1}')
                  echo "Found Container ID: ${ID}, Stopping and removing it...."
                  docker container stop ${ID} || true && docker container rm ${ID} || true
                done
                docker stop webapp || true && docker rm webapp || true
                docker build -t omarmansor/phpwebsite:$BUILD_NUMBER .
                echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin               	
                docker push omarmansor/phpwebsite:$BUILD_NUMBER
                docker run -itd -p 8080:80 --name webapp omarmansor/phpwebsite:$BUILD_NUMBER'''
            }
            post { 
                failure { 
                    echo 'Failure!, removing running container....'
                    sh returnStatus: true, script: ''' docker rm -f webapp || true'''
                }
                cleanup {
                    echo 'clean up workspace'
                    cleanWs cleanWhenFailure: true
                    echo 'Logging out from docker hub'
    			    sh 'docker logout'
                }
                
            }
            
        }

    }

}