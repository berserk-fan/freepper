# things to remember

1. name should be empty on creation

# secrets. env file population
.env file is populated on ec2
there is .env.template file which should have `PREFIX=` declaration
```text
PREFIX=/${ENV}/
SERVER_PORT=
DB_HOST=
```
on ec2 instance we will assign parameters with value from parameter store. e.g. `SERVER_PORT=get_from_param_store("$prefix$SERVER_PORT")`
in env file you will have ENV var which means env. It's either `Prod` or `Dev`

