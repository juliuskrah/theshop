# Setting up credentials for registry

To setup credentials to a private registry, run the following command:

`kubectl create secret docker-registry regcred --docker-server=<your-registry-server> --docker-username=<your-name> --docker-password=<your-pword> --docker-email=<your-email>`

Taken from [Kubernetes](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/)

# Setting up logging

To setup logging, use the following resource - https://docs.aws.amazon.com/eks/latest/userguide/fargate-logging.html

# Amazon EKS - Fargate

Use the `eksctl` tool to create a cluster

## Scheduling pods

To schedule a pod, you need to create a `fargate profile`. If labels are applied to the profile, then these same
labels need to be present on the `podspec` - https://docs.aws.amazon.com/eks/latest/userguide/fargate-profile.html

## Setting up PersistentVolume

When a pod needs access to a volume, you can create a `PeristentVolume` (PV) on AWS. The following options are available

- Amazon EBS
- Amazon EFS
- Amazon FSx for Lustre

Only EFS is supported on Fargate clusters. At the time of this writing, only `static volumes` can be provisioned:
This means PV must be created and a `PersistentVolumeClaim` bound before it can be used by a pod - https://docs.aws.amazon.com/eks/latest/userguide/efs-csi.html

## Networking

In order to expose a pod over the internet, an Elastic LoadBalancer must be used. Three types of load balances are
available:

- Classic LoadBalancer (CLB): `deprecated`
- Network LoadBalancer (NLB): Layer 4 transport
- Application LoadBalancer (ALB): Layer 7 transport

To use an NLB, you have to create a k8s `Service` resource of type `LoadBalancer`.  
To use an ALB, you have to create a k8s `Ingress` resource.

### Creating a Loadbalancer for your cluster

Following these [instructions](https://docs.aws.amazon.com/eks/latest/userguide/enable-iam-roles-for-service-accounts.html) to create an IAM OIDC provider for the cluster

Next enable the `AWS Loadbalancer Controller` - https://docs.aws.amazon.com/eks/latest/userguide/aws-load-balancer-controller.html

> 
  If you encounter an error, first create the `fargate profile`:  
  `eksctl create fargateprofile --cluster shoperal-dev-cluster --name cert-default --namespace cert-manager`.  
  Delete previously created resources:  
  `kubectl delete -f https://github.com/jetstack/cert-manager/releases/download/v1.0.2/cert-manager.yaml`.  
  Add them again `kubectl apply --validate=false -f https://github.com/jetstack/cert-manager/releases/download/v1.0.2/cert-manager.yaml` and wait for
  cert-manager pods to transition to `running`.  
  Run the following:  
  `kubectl delete -f v2_1_2_full.yaml`   
  `kubectl apply -f v2_1_2_full.yaml`


> 
  If the instructions above don't work incase of a `fargate cluster`, use the instructions at Premium Support:
  https://aws.amazon.com/premiumsupport/knowledge-center/eks-alb-ingress-controller-fargate/

In summary, run the following commands:

- `curl -o alb_ingress_iam_policy.json https://raw.githubusercontent.com/kubernetes-sigs/aws-alb-ingress-controller/v1.1.4/docs/examples/iam-policy.json`
- `aws iam create-policy --policy-name AlbIngressControllerIAMPolicy --policy-document file://alb_ingress_iam_policy.json` 
- `eksctl create iamserviceaccount --name alb-ingress-controller --namespace kube-system --cluster shoperal-dev-cluster --attach-policy-arn <replace with arn from step above> --approve --override-existing-serviceaccounts`
- `eksctl get iamserviceaccount --cluster shoperal-dev-cluster --name alb-ingress-controller --namespace kube-system`
- `curl -O https://raw.githubusercontent.com/kubernetes-sigs/aws-alb-ingress-controller/v1.1.4/docs/examples/alb-ingress-controller.yaml`
- Make changes to this file as documented
- Additionally, you will be required to tag two public or private subnets in this VPC with the following tags:
  kubernetes.io/role/elb=1  
  kubernetes.io/cluster/eksctl-rep=shared
- Alternatively you can use annotations for subnet discovery: https://kubernetes-sigs.github.io/aws-load-balancer-controller/guide/ingress/annotations/#subnets
- `kubectl apply -f alb-ingress-controller.yaml`
