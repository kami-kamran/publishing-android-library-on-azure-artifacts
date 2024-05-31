# publishing-android-library-on-azure-artifacts
**Publish an Android Library to Azure
Artifacts Using Mavin (Gradle Plugin)**

**Azure Artifacts** enables developers to share and consume packages from different feeds and public registries. Packages can be shared within the same team, the same organization, and even publicly. Azure Artifacts supports multiple package types such as npm, Python, Maven, and Universal Packages.
We are interested in the Maven support because it's the type of the repository that we can publish our existing maven android library to and to do that we need to configure the Azure DevOps by following the next steps:
if you don't have any project on azure you need to make a new azure project on Azure DevOps. This project will be called "CommonUtils", just like the code library you plan to store there.

If you're new to Azure DevOps and unsure how to create a workspace, there's a guide available 
Create a project - Azure DevOps
Learn how to create a new project where your team can plan, track progress, and collaborate on building software…docs.microsoft.com
Once you follow those steps and create your new workspace, you should see it listed when you log in to Azure DevOps.
image
2. On the left-hand side menu, click "Artifacts". This will take you to a list of all the feeds (repositories) in your project. The default feed will be named "CommonUtilsDemo" (created specifically for this example project and organization).
What is Azure Artifacts Feeds?
In Azure DevOps, Artifacts Feeds act like storage bins for your code. You can organize different types of code (like npm, NuGet) together in one bin, and control who gets to access them. This guide explains Feeds in more details. 
3. You can either use the existing feed named "CommonUtilsDemo" to publish your library, or create a new one entirely. We'll stick with the default feed "CommonUtilsDemo" for this example.
4. Once you've chosen a feed (existing or newly created), you'll see a message prompting you to connect. A button labeled "Connect to feed" will be available for this purpose. 
image
Clicking "Connect to feed" brings up a menu of supported package types. Under "Maven," choose "Gradle" (instead of just Maven) to proceed.
image
5. After selecting "Gradle" from the package type list, a project setup guide will appear on the right side. Look within the guide for a section labeled "maven{} block". Inside this block, you'll find variables named "url" and "name" with their corresponding values. Copy and save those values - we'll use them later to configure the repositories block in your project's build script.
image
6. To publish your Android library, you'll need a personal access token. This token acts like a key for authentication. You can find instructions to create one within the project setup guide that appeared on the right side in the previous step. This guide might redirect you to the Personal Access Tokens page, or you can follow a separate guide for creating a new token. Remember to save this token for later use.
image
7. In Android Studio, navigate to your project's root directory. Here, create a new file named "azure-configs.properties". Paste the content below into this file, replacing the bracketed placeholders with the actual values you obtained in the previous steps:
Replace [name of your feed] with the name you chose (or kept as "CommonUtilsDemo").
Replace [personal access token] with the token you created.
Replace [url of the feed] with the URL you copied from the project setup guide.

azureMavenAccessToken=[you personal access token]
 userName=[name of the feed]
 repositoryUrl=[url of the feed]
image
Note: Don't try to use the personal access token as it's created for the purpose of this article only and deleted right after I finished the article for security reasons off course ;)
Note: you shouldn't check the "azure-configs.properties" in version control because doing so expose the personal access token specially if it's an open source project. I only did that to provide the readers of this article with complete sample project which includes this file.
Configuring the Android Library with Maven Publish Plugin
First let's assume that you already created and have an existing android library configured with gradle (if you didn't create the android library check the this article.
Let's start by adding the Maven Publish Plugin to the android library by adding this plugin to the plugins section in build.gradle file in the library module (which is the gradle module for our existing library.
image
also  add the following code right inside the android block inside the build.gradle file of the library module
afterEvaluate {
        publishing {
            publications {

                create<MavenPublication>("release") {
                    from(components["release"])
                    groupId = "com.sample.library"
                    artifactId = "mytest"
                    version = "0.0.1"
                }
            }
            repositories {
                maven {
                    name = "feedName"
                    url =uri(`keystoreProperties`.getProperty("repositoryUrl"))
                    credentials {
                        username= keystoreProperties.getProperty("userName")
                        password =keystoreProperties.getProperty("azureMavenAccessToken")
                    }
                }
            }
        }

    }
image
Here's an explanation of the code block in simpler terms:
Creating the Package for Publishing:
release(MavenPublication) { }: This line sets up a section for defining how our library will be published. Think of it as creating a box to hold all the information for our library. We're calling this box "release".
What Goes in the Box:
from components.release: This line tells Gradle what to put inside the "release" box. In our case, it's the final, built version (the "release" component) of our library, along with any additional information it needs.
Adding Labels to the Box:
groupId 'com.example': This is like a category label for our library. It helps others find similar libraries (think "brand name"). You can replace "com.example" with your own group ID.
artifactId 'test-release': This is the specific name of our library within the group (think "product name"). Here, "commonutils" is the library name, and "release" indicates it's the final version.
version '0.0.1': This is the version number of our library. You can update this as you make changes and release new versions.
maven {}: indicate the type of the repository and in this case it's a maven repository.
name = 'feedName': determine the name of the maven repository (which will be used in generated tasks by gradle).
url 'https://pkgs.dev.azure.com/': determine the url location of the maven repository that we are going to publish to it.
credentials {}, username "user name" and password "token": configure any authentication details that are required to connect to the repository url defined previously (will be discussed in details later).
Note:
Gradle Needs a Storage Locker:
Gradle interacts with repositories, which are like storage lockers for code. It needs to know where these lockers are and how to access them.
Providing the Lockers' Info:
You tell Gradle about these lockers in the repositories block of your build script. Here you specify:
The type of locker (e.g., Maven repository)
The locker's location (web address)
Any login details needed to access the locker (if private)
Multiple Lockers are Okay:
You can define several lockers in the repositories block, as long as each has a unique name.
One Locker Without a Name:
If you only have one locker, you don't need to give it a name. Gradle will call it "Maven" by default.
image
By adding this block of code we have enabled the gradle build script to read the values from the file we created and use them with the maven publish gradle plugin in the publishing block.
After making these changes, Android Studio might ask you to "Sync Now". This helps Gradle refresh its understanding of your project with the new configuration.

Congratulations! You've now configured Azure Artifacts, prepared it for your library, and told Gradle how to connect using the secret key. You're ready to publish your library in the next step!
Publishing the Android Library to Azure Artifacts
In Android Studio, there's a window that shows all the tasks Gradle can perform for your project. open that Gradle window. This window is called the Gradle tool window. It will display tasks for:
The entire project (often called the "root project")
The main application module (usually named "app")
Your library module ("our library" in this case)

image
Click on the arrow next to 'our Library' -> 'Tasks' -> 'Publishing'. You will find the tasks we mentioned before which the Maven Publish Gradle Plugin generate for us for the 'common-utils'. We can double click on any one of these tasks to trigger the gradle to execute this task or click on the task called 'publish' to trigger all the tasks.
With everything prepared, it's time to publish your Android library! In the Gradle tool window (previously explained if needed), find the task named "publishReleasePublicationToAzureRepository". This long name essentially means "publish the final version of your library to the Azure repository you configured earlier." To initiate publishing, simply double-click this task within the Gradle tool window. This will instruct Gradle to use the configured settings and deploy the release build of your library to Azure Artifacts. 
image
Navigate to your project on Azure DevOps and open the Artifacts service and will find the your published library.
Congrats!, you have published your existing android library on Azure Artifacts and you can now use it in your android projects
Using the Published Android Library in Another App
i. open your settings.gradle file and add 
maven {
    url =uri("our maven uri")
  //credentials if require
}
ii. add our library dependency inside app build.gradle file under dependency block
Sync now build.gradle and To make sure that everything works fine you can build and run the app. Which means that we added the published utils android library from Azure Artifacts to the app module and used it successfully in another app.
Wrapping it up
This article guided you through the process of publishing an existing Android library to Azure Artifacts using the Maven Publish Gradle Plugin. You learned how to configure the plugin, connect to Azure Artifacts, and finally deploy your library. The article also explained how to use the published library in another Android application.
