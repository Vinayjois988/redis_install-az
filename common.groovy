import java.util.regex.Matcher;		
import java.util.regex.Pattern;		
import groovy.json.JsonSlurper;
import groovy.json.JsonSlurperClassic
import groovy.json.*

import java.util.Map;
import java.io.Reader;
import java.util.HashMap;


def Login() {
     withCredentials([azureServicePrincipal('1')]) {
       sh 'az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET -t $AZURE_TENANT_ID '
    }
}
 def createrg(def rgname,def rglocation) {
          sh """ az group create --name ${rgname} --location ${rglocation}"""
     }

 def createvm(def redis_vmname,def vmrg,def apassword,def ausername,def autype,def vmlocation,def nsgrule,def disk){
      sh """ az vm create -n ${redis_vmname} -g ${vmrg} --nsg-rule ${nsgrule} --authentication-type ${autype} --admin-password ${apassword} --os-type linux --admin-username ${ausername}  """
 }
def checkname(def name_storage){
     sh """ az storage account check-name --name ${name_storage} """
}
def createSA(def name_storage,def rgname){
sh """az storage account create -n ${name_storage} -g ${rgname} --sku 'Standard_LRS' """

}
def createSAC(def st_cont_name,def name_storage){
sh """az storage container create -n ${st_cont_name} --public-access 'blob' --account-name ${name_storage}"""
}
def Downloadteplate(def Tname,def Frgname){
     sh """az group deployment export --name ${Tname} --resource-group ${Frgname} """
}
def uploadARM(def ARMname,def ACCname,def st_cont_name){
     sh """ sudo az storage blob upload --container-name ${st_cont_name} --account-name ${ACCname}  --name ${ARMname} --file /home/sachin/Jenki-Pipe/Service/Ubuntu-ARM.json"""
}
def CreateNIC(def rgname,def vnetname,def sname,def nic_name,def nsgname){
     sh """ az network nic create -g ${rgname} --vnet-name ${vnetname} --subnet ${sname} -n ${nic_name} --network-security-group ${nsgname}""" 
}
     def CreatINFRA(def  vmusrname,def vmpasswd,def rglocation,def nic_name,def VMNAME,def vnetname,def rgname,def virtualMachineSize,def nsgname,def sname,def iptype,def nsg_rule_name){
     sh """ sudo az group deployment create --resource-group vinay --template-uri `sudo cat /tmp/sas` --parameters 'adminUsername=${vmusrname}' 'adminPassword=${vmpasswd}' 'location=${rglocation}' 'networkInterfaceName=${nic_name}' 'virtualMachineName=${VMNAME}' 'virtualNetworkName=${vnetname}' 'virtualMachineRG=${rgname}' 'virtualMachineSize=${virtualMachineSize}' 'networkSecurityGroupName=${nsgname}' 'publicIpAddressName=redis' 'addressPrefixes=172' 'subnetName=${sname}' 'osDiskType=EncryptionAtRestWithPlatformKey' 'publicIpAddressType=${iptype}' 'subnets=default' 'networkSecurityGroupRules=${nsg_rule_name}' 'sku=basic'"""
}
def CreateNSG(def nsgname,def rgname,def rglocation){
     sh """ az network nsg create -n ${nsgname} -g ${rgname} -l ${rglocation} """
}
def Createvnet(def vnetname,def rgname,def rglocation){
     sh """ az network vnet create -n ${vnetname} -g ${rgname} -l ${rglocation} """
}
def CreateSNET(def sname,def rgname,def vnetname){
     sh """ az network vnet subnet create -n ${sname} -g ${rgname} --vnet-name ${vnetname} --address-prefixes 10.0.0.0/24 """
}
def CreateNSGRule(def nsgname,def nsg_rule_name,def rgname,def open_port,def action,def protocol ,def Discri){
     sh """ az network nsg rule create --nsg-name ${nsgname} -n ${nsg_rule_name} -g ${rgname} --priority 110 --source-address-prefixes '*' --source-port-ranges '*' --destination-address-prefixes '*' --destination-port-ranges ${open_port} --access ${action} --protocol ${protocol} --description "${Discri}" """
}
def CreaUbunut(def adminUsername,def adminPasswordOrKey,def location,def virtualMachineName,def subnetName,def networkSecurityGroupName,def authenticationType,def ubuntuOSVersion,def VmSize,def virtualNetworkName){
     sh """ az group deployment create --resource-group vinay --template-uri `sudo cat /tmp/SAS | tr '"' ' '` --parameters 'adminUsername=${adminUsername}' 'adminPasswordOrKey=${adminPasswordOrKey}' 'location=westindia'  'vmName=${virtualMachineName}' 'VmSize=${VmSize}' 'networkSecurityGroupName=${networkSecurityGroupName}' 'subnetName=${subnetName}' 'virtualNetworkName=${virtualNetworkName}' 'authenticationType=${authenticationType}' 'ubuntuOSVersion=${UbuntuOsVersion}' """
}
return this
