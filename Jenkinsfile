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
        // 빌드 대상 서비스를 리스트로 관리
        SERVICES = "auth-service,store-service,order-service,gateway-service"
    }

    stages {
        stage('Parallel Gradle Build') {
            steps {
                script {
                    def buildTasks = [:]
                    def serviceList = env.SERVICES.split(',')
                    
                    serviceList.each { service ->
                        def serviceName = service.trim()
                        if (shouldBuild(serviceName)) {
                            buildTasks[serviceName] = {
                                container('gradle') {
                                    sh "chmod +x gradlew"
                                    sh "./gradlew :${serviceName}:clean :${serviceName}:build -x test"
                                }
                            }
                        }
                    }
                    // Gradle 빌드만 병렬로 실행하여 시간 단축
                    parallel buildTasks
                }
            }
        }

        stage('Sequential Image Push') {
            steps {
                script {
                    def serviceList = env.SERVICES.split(',')
                    serviceList.each { service ->
                        def serviceName = service.trim()
                        if (shouldBuild(serviceName)) {
                            // Kaniko 푸시는 하나씩 순차적으로 진행하여 충돌 방지
                            pushImage(serviceName)
                        }
                    }
                }
            }
        }
    }
}

// 빌드 대상인지 판별하는 함수
def shouldBuild(String serviceName) {
    def tagMap = [
        'auth-service': 'auth',
        'store-service': 'store',
        'order-service': 'order',
        'gateway-service': 'gateway'
    ]
    def paramName = "FORCE_${tagMap[serviceName].toUpperCase()}"
    
    return params.FORCE_BUILD_ALL || params."${paramName}" || 
           currentBuild.changeSets.any { set -> set.items.any { it.path.contains(serviceName) || it.path.contains("common/") } } ||
           hasCommitTag(tagMap[serviceName])
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

def pushImage(String serviceName) {
    container('kaniko') {
        echo "Pushing image for ${serviceName}..."
        sh """
        /kaniko/executor --context ${WORKSPACE} \
            --dockerfile ${WORKSPACE}/${serviceName}/Dockerfile \
            --destination ${REGISTRY}/${PROJECT}/${serviceName}:${BUILD_NUMBER} \
            --skip-tls-verify
        """
    }
}
