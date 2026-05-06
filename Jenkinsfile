pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: gradle
    image: gradle:8.7-jdk21
    command: ["sleep"]
    args: ["infinity"]
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ["sleep"]
    args: ["infinity"]
    volumeMounts:
    - name: harbor-token
      mountPath: /kaniko/.docker
  volumes:
  - name: harbor-token
    secret:
      secretName: harbor-secret
      items:
      - key: .dockerconfigjson
        path: config.json
'''
        }
    }

    environment {
        REGISTRY = "192.168.0.120:8080"
        PROJECT  = "green-eats"
    }

    stages {
        stage('Build and Push Services') {
            parallel {
                // [1] Auth Service
                stage('Auth-Service') {
                    // when { anyOf { changeset "auth-service/**"; changeset "common/**" } }
                    steps {
                        script { buildAndPush("auth-service") }
                    }
                }
                // [2] Store Service
                stage('Store-Service') {
                    // when { anyOf { changeset "store-service/**"; changeset "common/**" } }
                    steps {
                        script { buildAndPush("store-service") }
                    }
                }
                // [3] Gateway Service
                stage('Gateway-Service') {
                    // when { anyOf { changeset "gateway-service/**"; changeset "common/**" } }
                    steps {
                        script { buildAndPush("gateway-service") }
                    }
                }
            }
        }
    }
}

// 중복 코드를 줄이기 위한 함수 정의
def buildAndPush(String serviceName) {
    container('gradle') {
        sh "chmod +x gradlew"
        sh "./gradlew :${serviceName}:clean :${serviceName}:bootJar"
    }
    container('kaniko') {
        sh """
        /kaniko/executor --context ${WORKSPACE} \
            --dockerfile ${WORKSPACE}/${serviceName}/Dockerfile \
            --destination ${REGISTRY}/${PROJECT}/${serviceName}:${BUILD_NUMBER} \
            --skip-tls-verify
        """
    }
}