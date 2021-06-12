import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonBuilder

node{
    
	def common
	def rootDir = pwd()
	def mp
	def createrg
	def rglocation
	def rgname
	def name_storage
	def st_cont_name
	def Template_name
	def Frgname
	def ARMname
	def turl
	def sas
	def vmusrname="${adminUsername}"
	def vmpasswd="${adminPassword}"
	def VMNAME="${virtualMachineName}"
	def vnetname="redis-vnet"
	def virtualMachineSize="${VmSize}"
	def iptype="${PublicIpType}"
	def open_port="${DestinationPort}"
	def action="${Actions}"
	def protocol="${Protocol}"
	def Discri="${Description}"
	def nsg_rule_name="MyNsg"
	def nic_name="MyNic"
	def nsg22="MyNsg"
	
		

	
	
	stage('Load common files'){
		checkout scm
		common=load "${rootDir}/Service/Redis/Pipes/Common.groovy"
		   }
	stage ('Loadning master properties'){
		mp = readProperties file: "${rootDir}/Service/Redis/Pipes/Master.properties"
		
		rgname=mp['name']
		rglocation=mp['location']
		//vmlocation=mp['location']
		nsgrule=mp['nsg_rule']
		apassword=mp['passwd']
		ausername=mp['username']
		autype=mp['autype']
		vmrg=mp['rg']
		redis_vmname=mp['vmname']
		disk_id=mp['disk_id']
		name_storage=mp['stac']
		st_cont_name=mp['st_cont_name']
		Tname=mp['Template_name']
		Frgname=mp['default-Rg']
		ARMname=mp['ARM_name']
		ACCname=mp['Acname']
		//turl=mp['temURl']
		sas=mp['SAS']
		nsgname=mp['nsgn']
		sname=mp['subnet-name']
		
	}
	
	stage ('Login Azure'){
		common.Login()
		}
	stage ('Creating resource group for redis'){
		common.createrg(rgname,rglocation)
	}
	stage ('Checking for name '){
		common.checkname(name_storage)
	}
	
	stage ('Creating a new storage account'){
		common.createSA(name_storage,rgname)
	}
	stage ('Create a Container in Storage Account'){
		common.createSAC(st_cont_name,name_storage)
	}
	stage ('Uploading a ARM template into Storage Account'){
		common.uploadARM(ARMname,ACCname,st_cont_name)
	}
	stage ('Generating new SAS token'){
		common.CreateSAS(name_storage,st_cont_name)
	}
	stage ('creating a Network Security Group'){
		common.CreateNSG(nsgname,rgname,rglocation)
	}
	stage ('Creating New nsg rules'){
		common.CreateNSGRule(nsgname,nsg_rule_name,rgname,open_port,action,protocol,Discri)
	}
	
	//stage ('creating vnet '){
	//	common.Createvnet(vnetname,rgname,rglocation)
	//}
	stage ('Creating new subnets '){
		common.CreateSNET(sname,rgname,vnetname)
	}
	stage ('create new NIC'){
		common.CreateNIC(rgname,vnetname,sname,nic_name,nsgname)
	}
	//stage ('converting SAS tocken'){
	//	sh """
	//}
		
	stage ('Creating redis Infra'){
		common.CreatINFRA(vmusrname,vmpasswd,rglocation,nic_name,VMNAME,vnetname,rgname,virtualMachineSize,nsgname,sname,iptype,nsg22)
	}
}
