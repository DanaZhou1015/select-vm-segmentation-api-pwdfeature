library('apac_mgmt_jenkins_library')
app = [
    // Docker related
    "app_port": "8080",
    "host_port": "8080",
    "dockerArgs": "-v /var/log/liveramp/:/tmp/acp-be -v /data/smartaudience-data/:/opt/acp-be/audience_data/",

    // Automated deployment
    "unique_identifier": [
        "vm": "app-seg",
        "b2b": "app-seg"
    ]
]
properties([
    parameters([
        choice(
            name: 'PRODUCT',
            choices: ['vm', 'b2b'],
            description: 'Select a product'
        ),
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'cert', 'prod'],
            description: 'Select an environment'
        ),
        choice(
            name: 'DEPLOY',
            choices: ["true", "false"],
            description: 'Deploy to servers'
        )
    ])
])
node {
    try {
        stage('Preparation') {
            tools.clean()
            sh 'env'
            sh 'whoami'
        }
        stage('GitHub: Checkout') {
            checkout([$class: 'GitSCM', branches: [[name: env.BRANCH_NAME]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: scm.userRemoteConfigs])
            sh 'ls -la'
        }
        stage('Custom: Adjustments') {
            sh """
            cp ./src/main/resources/application_${params.PRODUCT}_${params.ENVIRONMENT}.properties ./src/main/resources/application.properties
            """
        }
        stage('Maven: Build') {
            withMaven(maven: 'Default') {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
            sh 'ls -la ./target'
        }
        (imageName, imageId) = tools.getImageInfo("java")
        pom = readMavenPom file: 'pom.xml'
        PACKAGE_VERSION = pom.version
        stage('Docker: Build & Push') {
            sh """
            gcloud -v
            docker -v
            mv ./target/${imageName}-${PACKAGE_VERSION}.jar ./target/app.jar
            """
            dockerTools.push(imageName, imageId)
        }
        stage('Docker: Deploy') {
            if (DEPLOY == "true") {
                dockerTools.automatedDeploy(imageName, imageId, app, constants.getInfrastructureMap())
            }
        }
    } catch (err) {
        tools.handleError(err)
    } finally {
        stage('Clean Up') {
            tools.clean()
        }
    }
}
