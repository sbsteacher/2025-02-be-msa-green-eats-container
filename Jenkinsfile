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

    parameters {
        booleanParam(name: 'FORCE_BUILD_ALL', defaultValue: false, description: '모든 서비스를 강제로 빌드합니다.')
        booleanParam(name: 'FORCE_AUTH', defaultValue: false, description: 'Auth-Service를 강제로 빌드합니다.')
        booleanParam(name: 'FORCE_STORE', defaultValue: false, description: 'Store-Service를 강제로 빌드합니다.')
        booleanParam(name: 'FORCE_ORDER', defaultValue: false, description: 'Order-Service를 강제로 빌드합니다.')
        booleanParam(name: 'FORCE_GATEWAY', defaultValue: false, description: 'Gateway-Service를 강제로 빌드합니다.')
    }

    environment {
        REGISTRY = "harbor.greenart.n-e.kr"
        PROJECT  = "green-eats"
    }

    stages {
        stage('Build and Push Services') {
            parallel {
                // --- [Auth-Service] ---
                stage('Auth-Service') {
                    when {
                        anyOf {
                            changeset "auth-service/**"
                            changeset "common/**"
                            expression { params.FORCE_BUILD_ALL || params.FORCE_AUTH }
                            expression { hasCommitTag("auth") }
                        }
                    }
                    steps {
                        script { buildAndPush("auth-service") }
                    }
                }

                // --- [Store-Service] ---
                stage('Store-Service') {
                    when {
                        anyOf {
                            changeset "store-service/**"
                            changeset "common/**"
                            expression { params.FORCE_BUILD_ALL || params.FORCE_STORE }
                            expression { hasCommitTag("store") }
                        }
                    }
                    steps {
                        script { buildAndPush("store-service") }
                    }
                }

                // --- [Order-Service] ---
                stage('Order-Service') {
                    when {
                        anyOf {
                            changeset "order-service/**"
                            changeset "common/**"
                            expression { params.FORCE_BUILD_ALL || params.FORCE_ORDER }
                            expression { hasCommitTag("order") }
                        }
                    }
                    steps {
                        script { buildAndPush("order-service") }
                    }
                }

                // --- [Gateway-Service] ---
                stage('Gateway-Service') {
                    when {
                        anyOf {
                            changeset "gateway-service/**"
                            changeset "common/**"
                            expression { params.FORCE_BUILD_ALL || params.FORCE_GATEWAY }
                            expression { hasCommitTag("gateway") }
                        }
                    }
                    steps {
                        script { buildAndPush("gateway-service") }
                    }
                }
            }
        }
    }
}

def hasCommitTag(String tag) {
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.size(); j++) {
            def entry = entries[j]
            def msg = entry.msg.toLowerCase()
            if (msg.contains("[build-all]") || msg.contains("[${tag}]")) {
                return true
            }
        }
    }
    return false
}

/**
 * [도움 함수] Gradle 빌드 및 Kaniko 이미지 푸시를 수행합니다.
 */
def buildAndPush(String serviceName) {
    // 1. Gradle 빌드 (JAR 생성)
    container('gradle') {
        sh "chmod +x gradlew" 
        // 요청하신 대로 clean 후 특정 서비스의 build(컴파일 포함)를 수행하며 테스트는 제외합니다.
        sh "./gradlew clean :${serviceName}:build -x test"
    }

    // 2. Kaniko 빌드 및 Harbor 푸시 (이미지 생성)
    container('kaniko') {
        sh """
        /kaniko/executor --context ${WORKSPACE} \
            --dockerfile ${WORKSPACE}/${serviceName}/Dockerfile \
            --destination ${REGISTRY}/${PROJECT}/${serviceName}:${BUILD_NUMBER} \
            --skip-tls-verify
        """
    }
}
