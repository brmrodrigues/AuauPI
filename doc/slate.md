--- 

title: Dogs adoption API - auaupi 

language_tabs: 
   - shell 

toc_footers: 
   - <a href='#'>Sign Up for a Developer Key</a> 
   - <a href='https://github.com/lavkumarv'>Documentation Powered by lav</a> 

includes: 
   - errors 

search: true 

--- 

# Introduction 

This is a API for dogs adoption. 

**Version:** 1.0.0 

# /DOGS
## ***GET*** 

**Summary:** shows all dogs available for adoption

**Description:** Shows all dogs available for adoption with resumed informations and a link with an image of him


### HTTP Request 
`***GET*** /dogs` 

**Parameters**

| Name | Located in | Description | Required | Type |
| ---- | ---------- | ----------- | -------- | ---- |
| specificDog | query | pass an optional id to search for a specific dog, providing detailed information | No | string |

**Responses**

| Code | Description |
| ---- | ----------- |
| 200 | search results matching criteria |
| 400 | bad input parameter |

## ***POST*** 

**Summary:** adds an dog

**Description:** Adds an dog to the list of dogs available

### HTTP Request 
`***POST*** /dogs` 

**Parameters**

| Name | Located in | Description | Required | Type |
| ---- | ---------- | ----------- | -------- | ---- |
| RegisterDog | body | New dog | No |  |
| adoptDog | query | pass an optional id to adopt a dog | No | string |

**Responses**

| Code | Description |
| ---- | ----------- |
| 201 | item created |
| 400 | invalid input, object invalid |
| 409 | an existing item already exists |

<!-- Converted with the swagger-to-slate https://github.com/lavkumarv/swagger-to-slate -->
