USE [master]
GO
/****** Object:  Database [MyTrips]    Script Date: 6.12.2016 22:16:55 ******/
CREATE DATABASE [MyTrips]
GO
USE [MyTrips]
GO
/****** Object:  Table [dbo].[BannedWords]    Script Date: 6.12.2016 22:16:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BannedWords](
	[word] [varchar](50) NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[GPSPoints]    Script Date: 6.12.2016 22:16:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[GPSPoints](
	[tripId] [varchar](10) NOT NULL,
	[pointOrder] [int] NOT NULL,
	[lat] [decimal](10, 6) NULL,
	[lng] [decimal](10, 6) NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Tags]    Script Date: 6.12.2016 22:16:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tags](
	[tripId] [varchar](10) NOT NULL,
	[tagOrder] [int] NOT NULL,
	[tag] [varchar](100) NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Trips]    Script Date: 6.12.2016 22:16:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Trips](
	[id] [varchar](10) NOT NULL,
	[name] [varchar](50) NULL,
	[description] [varchar](255) NULL,
	[lenght] [decimal](20, 6) NULL
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Users]    Script Date: 6.12.2016 22:16:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[userName] [varchar](10) NOT NULL,
	[password] [varchar](60) NULL,
	[email] [varchar](50) NULL
) ON [PRIMARY]

GO

