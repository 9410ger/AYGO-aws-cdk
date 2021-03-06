package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.Role;
import java.util.ArrayList;
import java.util.List;

public class AwsCdkStack extends Stack {
    public AwsCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        for(int i = 0; i < 3; i++){
            instanceEC2Machine(i);
        }

    }

    public IVpc createVpc(String index){

        SubnetAttributes subnetAttributes = SubnetAttributes.builder()
                .availabilityZone("us-east-1")
                .subnetId("subnet-0d07db45adf958803").build();

        ISubnet subnet = Subnet.fromSubnetAttributes(this,"my-subnet" + index,subnetAttributes);

        List<ISubnet> subnetList = new ArrayList<>();
        subnetList.add(subnet);
        VpcLookupOptions vpcLookupOptions = VpcLookupOptions.builder()
                .vpcId("vpc-03689d268ef2286b0").build();
        IVpc vpc = Vpc.fromLookup(this,"my-vpc" + index,vpcLookupOptions);
        SubnetSelection subnetSelection = SubnetSelection.builder()
                .subnets(subnetList).build();
        vpc.selectSubnets(subnetSelection);
        return vpc;
    }

    public ISecurityGroup createSecurityGroup(IVpc vpc, String index){
        return SecurityGroup.fromLookup(this,"my-sg-cli" + index,"sg-0a29ac6bb75d0fc75");

    }
    
    public IMachineImage createMachineImage(){
        return MachineImage.latestAmazonLinux();
    }

    public IRole createRole(String index){
        String roleArn = "arn:aws:iam::972353547499:role/EMR_EC2_DefaultRole";
        return Role.fromRoleArn(this,"my-role" + index,roleArn);
    }

    public void instanceEC2Machine(int index){
        String indexString = Integer.toString(index);
        IVpc iVpc = createVpc(indexString);
        ISecurityGroup securityGroup = createSecurityGroup(iVpc,indexString);
        IMachineImage iMachineImage = createMachineImage();
        IRole role = createRole(indexString);
        InstanceType instanceType = new InstanceType("t2.micro");


        InstanceProps instanceProps = InstanceProps.builder()
                .keyName("myKey")
                .instanceType(instanceType)
                .machineImage(iMachineImage)
                .securityGroup(securityGroup)
                .vpc(iVpc)
                .role(role)
                .build();
        Instance instance = new Instance(this,"my-vp" + index,instanceProps);
        instance.addUserData("sudo yum update -y");
        instance.addUserData("sudo yum install git -y");
        instance.addUserData("sudo yum install docker -y");
        instance.addUserData("sudo service docker start");
        instance.addUserData("sudo git clone https://github.com/9410ger/AYGO-taller1");
        instance.addUserData("cd AYGO-taller1");
        instance.addUserData("sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose");
        instance.addUserData("sudo mv /usr/local/bin/docker-compose /usr/bin/docker-compose");
        instance.addUserData("sudo chmod +x /usr/bin/docker-compose");
        instance.addUserData("sudo docker-compose up");

    }




}
