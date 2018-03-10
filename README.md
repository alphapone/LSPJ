# LSPJ

Local stored procedures for Java

## Purpose

LSPJ project is purposed to get a simple tool for compensation some design restrictions such as not using server side DBMS stored procedures.

## Structure

LSPJ consists from two main parts: 

 * macro processor purposed to extend SQL scripts with parameters, loading  from resource and including with parameters capabilites
 * JDBC driver proxy purposed to provide local stored SQL scripts using for any existing application

## Status

This project is in early development now:

 * sql macro parser is written (comments and sql text constants are supported) 
 * sql macro executor and api is in todo list
 * JDBC driver proxy (for debugging SQL Macro directly from Netbeans and SQL Developer) is in todo list (will be done after sql macro executor) 