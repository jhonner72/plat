CREATE TABLE [dbo].[nabusersynch_delta](
	[user_id] [varchar](50) NOT NULL,
	[first_name] [varchar](100) NULL,
	[last_name] [varchar](100) NULL,
	[user_email] [varchar](100) NULL,
	[user_group] [varchar](200) NULL,
	[data_date] [date] NULL,
	[ops_type] [varchar](20) NULL
) ON [PRIMARY]