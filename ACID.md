# ACID
Свойства транзакционной базы данных. 

## ATOMICITY 
Несколько записей в базу данных, не хотим чтобы они были сделаны частично

`start transaction;`  
`update account SET balance = balance - 100 where id = 1;`  
`update account SET balance = balance - 100 where id = 2;`
`commit;`

Некоторые предлагают термин ABORTABILITY - возможность прервать транзацию в любом месте без ущерба для консистентности данных;

## CONSISTENCY
Eventual Consistency - если база реплицируется на несколько машин, то данные когда-нибудь дойдут до всех реплик.  

CAP-theorem 
* Consistency (immediate) != Eventual Consistency. Если мы гарантируем C из CAP, то в любой момент времени мы должны получить одинаковый сет данных с разных реплик.

В контексте ACID Консистентность значит, что данные находятся в :good state:  

Правило консистентности значит, что если перед выполнением транзации данные находились в good state, то после выполнения транзации они тоже будут находится в good state;


## ISOLATION

## DURABILITY