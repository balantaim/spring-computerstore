# Computer Store - Spring Fullstack Demo Project

### License: [Apache 2.0](LICENSE)

## Description

<p>"Computer Store" is a cutting-edge e-commerce platform built using Java Spring framework, hosted on the reliable AWS (Amazon Web Services) cloud platform. Designed to cater to tech-savvy consumers, this demo project showcases a seamless online shopping experience for computer hardware and accessories enthusiasts.</p>

## Software Stack

<p><b>Software tools:</b> Java 21 (mandatory for Virtual Threads), Spring (Web MVC, JPA, Validation, Actuator, Data REST, Security, Aspect Oriented Programming), Thymeleaf, Lombok, Bulma (Boostrap competitor), Maven</p>
<p><b>Database:</b> MySQL</p>
<p><b>Cloud Platform:</b> AWS Elastic Beanstalk</p>

### Key Features:
<ol>
    <li><b>User Authentication and Authorization:</b> Users can register, log in, and manage their profiles securely. Role-based access control ensures appropriate access levels for both customers and administrators.</li>
    <li><b>Product Catalog:</b> Explore a vast array of computer products conveniently categorized for easy navigation. From CPUs to graphic cards, peripherals to software, Computer Store offers an extensive selection to meet diverse needs.</li>
    <li><b>Search Functionality:</b> Find desired products quickly with the powerful search functionality. Users can search by product name, category, brand, or specifications to locate exactly what they're looking for.</li>
    <li><b>Product Details:</b> Detailed product pages provide comprehensive information about each item, including specifications, images, customer reviews, and related products, aiding informed purchasing decisions.</li>
    <li><b>Add to Cart:</b> Seamlessly add products to the shopping cart with just a click. Users can easily adjust quantities and remove items as needed before proceeding to checkout.</li>
    <li><b>Shopping Cart Management:</b> The shopping cart keeps track of selected items throughout the browsing session. Users can review their selections, update quantities, and proceed to checkout when ready.</li>
    <li><b>Checkout Process:</b> Streamlined checkout process ensures a hassle-free transaction experience. Users can enter shipping and billing information, select preferred payment methods, and review their order summary before confirming the purchase.</li>
    <li><b>Payment Gateway Integration:</b> Secure payment processing is facilitated through integration with popular payment gateways, ensuring confidentiality and reliability of transactions. Multiple payment methods, including credit/debit cards and digital wallets, are supported.</li>
    <li><b>Order Management:</b> Users can view order history, track the status of their orders, and receive notifications at each stage of the fulfillment process. Administrators have access to comprehensive order management tools for efficient processing and tracking.</li>
    <li><b>Responsive Design:</b> Computer Store is built with a responsive design, ensuring optimal performance and user experience across various devices and screen sizes, including desktops, laptops, tablets, and smartphones.</li>
    <li><b>Admin Panel:</b> Administrators have access to a robust admin panel for managing products, categories, users, orders, and other aspects of the platform. Advanced analytics and reporting tools enable data-driven decision-making and performance monitoring.</li>
</ol>

## Setup the project

<ol>
    <li>Install Java 21 LTS (OpenJDK Corretto)</li>
    <li>Connect to your MySQL DB</li>
    <li>Run SQL query "computer-store-test.sql"</li>
    <li>Select profile from "application.properties" (test/prod profile)</li>
    <li>Test the project in your favorite IDE</li>
    <li>Optionally run "fake-data.sql" in your database for fake products</li>
    <li>Create execution jar from the terminal by using "mvn clean package"</li>
    <li>Your project is ready in ./target directory</li>
</ol>

## Prepare cloud platform

<ol>
    <li>Register/Login to AWS</li>
    <li>Search for Elastic Beanstalk</li>
    <li>Create new environment with support for Java</li>
    <li>Disable DB services (We are using database outside from AWS cloud)</li>
    <li>Disable Alarm service</li>
    <li>Add role to the environment (Check how to create a new role below)</li>
    <li>Upload the jar file when the environment is ready</li>
</ol>

## Create a new role for Elastic Beanstalk
<p><b>Path:</b> AWS Console > IAM > Roles > Create role</p>
<p><b>Add all three permissions:</b></p>
<ul>
    <li>AWSElasticBeanstalkWebTier</li>
    <li>AWSElasticBeanstalkWorkerTier</li>
    <li>AWSElasticBeanstalkMulticontainerDocker</li>
</ul>

## Project optimisations

<ul>
    <li>Gzip conversion</li>
    <li>Enable cacheable static assets: *.js, *.css, image/**</li>
</ul>

## Test Information

### Test users

1. Role: customer; <br>Username: <i>abv@abv.bg</i><br>Password: <i>test</i>
2. Role: customer, manager; <br>Username: <i>manager@abv.bg</i><br>Password: <i>test</i>
3. Role: customer, manager, admin; <br>Username: <i>admin@abv.bg</i><br>Password: <i>test</i>

### API testing

> [!IMPORTANT]
> Use only for TEST profile only!
> Be sure CORS is disabled!

<p>Postman collection: <a href="https://github.com/balantaim/spring-computerstore/blob/master/postman/computer-store.postman_collection.json">postman.json</a></p>

### Actuator endpoint

<p>Actuator link: <a href="http://computer-store.eu-north-1.elasticbeanstalk.com/page/actuator">/page/actuator</a></p>

### Limitations

<ul>
    <li><b>Email Sender</b> is not implemented for this project! New user profiles are verified by default. You could check how to implement Email Sender from this project: <a href="https://github.com/balantaim/EmailSender">EmailSenderRepository</a></li>
</ul>

## Useful tools

<ul>
    <li>MySQL Workbench user interface tool URL: <a href="https://dev.mysql.com/downloads/workbench">MySQL Workbench</a></li>
    <li>Multiple DB user interface tool URL: <a href="https://dbeaver.io/download">DBever - Universal Database Tool</a></li>
</ul>

## Production Website

### You could check the latest changes online on the production link below

<p>AWS Elastic Beanstalk production link: <a href="http://computer-store.eu-north-1.elasticbeanstalk.com/">computer-store.eu-north-1.elasticbeanstalk.com</a></p>
